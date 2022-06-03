package com.spring.elastic.controller;

import com.spring.elastic.document.Product;
import com.spring.elastic.service.ProductService;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping()
    public void save(@RequestBody final List<Product> products) {
        service.createProductIndexBulk(products);
    }

    @PostMapping("/alias/{aliasName}")
    public boolean indexAliasRequest(@PathVariable final String aliasName) {
        return service.indexAliasRequest(aliasName);
    }

    @GetMapping("/questr")
    public List<Product> findById(@RequestBody final String queryString) {
        return service.queryString(queryString);
    }

    @GetMapping("/tophits")
    public List<Product> topHitsAggregation() {
        return service.topHitsAggregation();
    }

    @GetMapping("/filter")
    public Map<String, Long> filterAggregation() {
        return service.filterAggregation();
    }

    @GetMapping()
    public List<Product> search(
            @RequestParam(name = "searchKey", required = false, defaultValue = "") String searchKey,
            @RequestParam(name = "c", required = false, defaultValue = "") String c,
            @RequestParam(name = "b", required = false, defaultValue = "") String b,
            @RequestParam(name = "pgt", required = false, defaultValue = "") Double pgt,
            @RequestParam(name = "plt", required = false, defaultValue = "") Double plt
    ) {
        return service.search(searchKey, c, b, pgt, plt);
    }
}