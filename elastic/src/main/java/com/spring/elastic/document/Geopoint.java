package com.spring.elastic.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "geopoint")
public class Geopoint {
    @Id
    private String id;

    @GeoPointField
    private double[] coordinate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
    }
}