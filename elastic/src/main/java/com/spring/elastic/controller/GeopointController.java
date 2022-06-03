package com.spring.elastic.controller;

import com.spring.elastic.document.Geopoint;
import com.spring.elastic.service.GeopointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geopoint")
public class GeopointController {
    private final GeopointService service;

    @Autowired
    public GeopointController(GeopointService service) {
        this.service = service;
    }

    @PostMapping()
    public void save(@RequestBody final List<Geopoint> geopoints) {
        service.createGeopointIndexBulk(geopoints);
    }
}
