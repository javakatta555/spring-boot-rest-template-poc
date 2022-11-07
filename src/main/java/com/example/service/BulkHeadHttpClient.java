package com.example.service;

import com.example.model.Rating;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static com.example.constant.BulkHeadConstant.RATING_SERVICE;

@Service
@Slf4j
public class BulkHeadHttpClient {

    @Autowired
    private BulkHeadService bulkHeadService;

    @Value("${bulkHeadEnabled}")
    private Boolean bulkHeadEnabled;

    @Autowired
    private RestTemplate restTemplate;

    public Rating ratingServiceClient() {
        Supplier<Rating> supplier = () -> restTemplate.getForObject("http://localhost:8081/rating", Rating.class);
        try {
            if (isBulkHeadEnabled(RATING_SERVICE))
                return bulkHeadService.getThreadPoolBulkheadMap().get(RATING_SERVICE).executeSupplier(supplier)
                        .toCompletableFuture().get();
            return supplier.get();
        }catch (InterruptedException | ExecutionException e) {
            log.error("[ratingServiceClient] Error in calling lead service with bulk head client", e);
        } catch (BulkheadFullException e) {
            log.error("[ratingServiceClient] Bulkhead Full {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    private boolean isBulkHeadEnabled(String name) {
        return bulkHeadEnabled && Objects.nonNull(bulkHeadService.getThreadPoolBulkheadMap()) &&
                !bulkHeadService.getThreadPoolBulkheadMap().isEmpty() &&
                Objects.nonNull(bulkHeadService.getThreadPoolBulkheadMap().get(name));
    }

}
