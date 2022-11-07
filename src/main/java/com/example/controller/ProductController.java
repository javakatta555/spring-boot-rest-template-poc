package com.example.controller;

import com.example.service.BulkHeadHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class ProductController {

    @Autowired
    private BulkHeadHttpClient bulkHeadHttpClient;

    @GetMapping(value = "/product")
    public void getProductById(){
        log.info("Inside Product Controller getProductById method");
        bulkHeadHttpClient.ratingServiceClient();
    }
}
