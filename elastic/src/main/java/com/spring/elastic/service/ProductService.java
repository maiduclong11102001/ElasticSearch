package com.spring.elastic.service;

import com.spring.elastic.document.Product;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ElasticsearchOperations operations;
    private final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200)));

    @Autowired
    public ProductService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public void createProductIndexBulk(final List<Product> products) {

        List<IndexQuery> queries = products.stream()
                .map(product ->
                        new IndexQueryBuilder()
                                .withId(product.getId())
                                .withObject(product).build())
                .collect(Collectors.toList());

        operations.bulkIndex(queries, IndexCoordinates.of("product"));
    }

    public List<Product> queryString(final String string) {
        Query query = new StringQuery(string);

        SearchHits<Product> searchHits = operations.search(query, Product.class);
        List<Product> products = new ArrayList<>();
        searchHits.forEach(x -> products.add(x.getContent()));
        return products;
    }

    public List<Product> search(final String searchKey, final String category, final String brand, final Double pgt, final Double plt) {
        Criteria criteria = new Criteria();
        if (!category.equals(""))
            criteria.subCriteria(new Criteria("category").is(category));
        if (!brand.equals(""))
            criteria.subCriteria(new Criteria("brand").is(brand));
        if (pgt != null)
            criteria.subCriteria(new Criteria("price").greaterThanEqual(pgt * 1000000));
        if (plt != null)
            criteria.subCriteria(new Criteria("price").lessThanEqual(plt * 1000000));
        if (!searchKey.equals(""))
            criteria.subCriteria(new Criteria("name").contains(searchKey));

        Query query = new CriteriaQuery(criteria);
//                .addSort(Sort.by("price").ascending());

        SearchHits<Product> searchHits = operations.search(query, Product.class);

        List<Product> products = new ArrayList<>();
        searchHits.forEach(x -> products.add(x.getContent()));
        return products;
    }

    public List<Product> topHitsAggregation() {

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] sortByCategory = {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder
                        (QueryBuilders.matchQuery("brand", "apple"), ScoreFunctionBuilders.weightFactorFunction(2)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder
                        (QueryBuilders.matchQuery("category", "tablet"), ScoreFunctionBuilders.weightFactorFunction(500)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder
                        (QueryBuilders.matchQuery("category", "phone"), ScoreFunctionBuilders.weightFactorFunction(200)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder
                        (QueryBuilders.matchQuery("category", "laptop"), ScoreFunctionBuilders.weightFactorFunction(100))
        };

        FunctionScoreQueryBuilder queryBuilder = QueryBuilders
                .functionScoreQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("price").gte(30)), sortByCategory)
                .boost(1).boostMode(CombineFunction.MULTIPLY).scoreMode(FunctionScoreQuery.ScoreMode.MULTIPLY);

        Query query = new NativeSearchQueryBuilder()
                .withSorts(SortBuilders.fieldSort("_score").order(SortOrder.DESC),
                        SortBuilders.fieldSort("price").order(SortOrder.DESC))
                .withQuery(queryBuilder).build();

        SearchHits<Product> searchHits = operations.search(query, Product.class);

        List<Product> products = new ArrayList<>();
        searchHits.forEach(x -> products.add(x.getContent()));
        return products;
    }

    public long filterAggregation() {
        FilterAggregationBuilder filter = AggregationBuilders.filter("filter_brand", QueryBuilders.termQuery("brand", "apple"));

        MaxAggregationBuilder max = AggregationBuilders.max("price").field("price");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().aggregation(filter);

        SearchRequest searchRequest = new SearchRequest().source(sourceBuilder);

        Filter filterBrand;

        Max maxtest;

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            filterBrand = aggregations.get("filter_brand");
            return filterBrand.getDocCount();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}