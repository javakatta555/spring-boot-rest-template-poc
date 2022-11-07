package com.example.controller;

import com.example.service.BulkHeadService;
import com.example.service.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("bulkhead")
@Slf4j
public class BulkHeadMetricsController {

    @Autowired
    private BulkHeadService bulkHeadService;

    @GetMapping("/metrics")
    public ResponseEntity<Map> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        bulkHeadService.getThreadPoolBulkheadMap().forEach((key, threadPoolBulkhead) -> response.put(key, threadPoolBulkhead.getMetrics()));
        return CustomResponse.ok(response);
    }

    @GetMapping("/refresh/{bulkHeadName}")
    public ResponseEntity<Map> refresh(@PathVariable String bulkHeadName) {
        log.info("Inside bulkhead metrics");
        bulkHeadService.refresh(bulkHeadName);
        return CustomResponse.ok();
    }

    @GetMapping("/refreshAll")
    public ResponseEntity<Map> refreshAll() {
        bulkHeadService.refreshThreadPoolBulkheadMap();
        return CustomResponse.ok();
    }
}
