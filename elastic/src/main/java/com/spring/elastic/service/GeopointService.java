package com.spring.elastic.service;

import com.spring.elastic.document.Geopoint;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
