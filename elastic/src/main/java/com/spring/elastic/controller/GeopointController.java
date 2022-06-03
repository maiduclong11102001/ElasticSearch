package com.spring.elastic.controller;

import com.spring.elastic.document.Geopoint;
import com.spring.elastic.service.GeopointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/getClose")
    public List<Map<String, Object>> geopointDistance() {
        return service.geopointDistance();
    }
}
