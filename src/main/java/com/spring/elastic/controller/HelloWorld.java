package com.spring.elastic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {


    @GetMapping("/")
    public String hello() {
        return "{ name: Long, age: 20, schools: {1: \"THPT Huong Tra\", 2: \"DHBK\"}}";
    }
}