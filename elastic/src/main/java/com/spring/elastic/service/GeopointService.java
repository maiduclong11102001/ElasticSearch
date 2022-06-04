package com.spring.elastic.service;

import com.spring.elastic.document.Geopoint;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeopointService {
    private final ElasticsearchOperations operations;

    @Autowired
    public GeopointService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public void createGeopointIndexBulk(final List<Geopoint> geopoints) {

        List<IndexQuery> queries = geopoints.stream()
                .map(geopoint ->
                        new IndexQueryBuilder()
                                .withId(geopoint.getId())
                                .withObject(geopoint).build())
                .collect(Collectors.toList());

        operations.bulkIndex(queries, IndexCoordinates.of("geopoint"));
    }

    public List<Map<String, Object>> geopointDistance() {
        Query query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
                .withSorts(SortBuilders.geoDistanceSort("coordinate", 10.880013415721521, 106.81087990196458).order(SortOrder.ASC))
                .build();

        SearchHits<Geopoint> geopoints = operations.search(query, Geopoint.class);

        List<Map<String, Object>> result = new ArrayList<>();

        for (SearchHit<Geopoint> geopoint : geopoints) {
            Map<String, Object> map = new HashMap<>();
            double distance = (double)geopoint.getSortValues().get(0);
            map.put("distance", ((double)Math.round(distance)/1000) + " km");
            map.put("content", geopoint.getContent());

            result.add(map);
        }

        return result;
    }
}