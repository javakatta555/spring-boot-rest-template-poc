package com.example.service;

import com.example.model.BulkHeadConfig;
import com.example.repository.BulkHeadRepository;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;


@Service
@Slf4j
public class BulkHeadService {

    @Getter
    private Map<String, ThreadPoolBulkhead> threadPoolBulkheadMap = new HashMap<>();

    @Autowired
    private BulkHeadRepository bulkHeadRepository;


    @Bean(name = "threadPoolBulkheadMap")
    public Map<String, ThreadPoolBulkhead> populateThreadPoolBulkheadMap() {
        return refreshThreadPoolBulkheadMap(); //initializing threadPoolBulkheadMap
    }

    public Map<String, ThreadPoolBulkhead> refreshThreadPoolBulkheadMap() {
        Map<String, ThreadPoolBulkhead> threadPoolBulkheadMap = new HashMap<>();
        List<BulkHeadConfig> bulkHeadConfigList = bulkHeadRepository.findAll();
        bulkHeadConfigList.forEach(bulkHeadConfig -> {
            if (bulkHeadConfig.isActive())
                threadPoolBulkheadMap.put(bulkHeadConfig.getId(), createThreadPoolBulkhead(bulkHeadConfig));
        });
        this.threadPoolBulkheadMap = threadPoolBulkheadMap;
        return threadPoolBulkheadMap;
    }


    private ThreadPoolBulkhead createThreadPoolBulkhead(BulkHeadConfig bulkHeadConfig) {
        Duration keepAliveDuration = ThreadPoolBulkheadConfig.DEFAULT_KEEP_ALIVE_DURATION;
        if (bulkHeadConfig.getKeepAliveDuration() > 0)
            keepAliveDuration = Duration.ofMillis(bulkHeadConfig.getKeepAliveDuration());
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(bulkHeadConfig.getMaxThreadPool())
                .queueCapacity(bulkHeadConfig.getQueueCapacity())
                .keepAliveDuration(keepAliveDuration)
                .build();
        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.of(config);
        return registry.bulkhead(bulkHeadConfig.getId());
    }

    private List<Map<String, Object>> getBulkheadList() {
        List<Map<String, Object>> bulkheadList = new ArrayList<>();
        this.threadPoolBulkheadMap.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("coreThreadPoolSize", value.getMetrics().getCoreThreadPoolSize());
            map.put("maximumThreadPoolSize", value.getMetrics().getMaximumThreadPoolSize());
            map.put("threadPoolSize", value.getMetrics().getThreadPoolSize());
            map.put("queueCapacity", value.getMetrics().getQueueCapacity());
            map.put("queueDepth", value.getMetrics().getQueueDepth());
            map.put("remainingQueueCapacity", value.getMetrics().getRemainingQueueCapacity());
            bulkheadList.add(map);
        });
        return bulkheadList;
    }

    public void refresh(String bulkHeadName) {
        Optional<BulkHeadConfig> bulkHeadConfig = bulkHeadRepository.findById(bulkHeadName);
        log.info("Printing bulkhead :{}",bulkHeadConfig);
        if (!bulkHeadConfig.isPresent())
            return;
        if (!bulkHeadConfig.get().isActive()) {
            this.threadPoolBulkheadMap.remove(bulkHeadName);
            return;
        }
        ThreadPoolBulkhead threadPoolBulkhead = createThreadPoolBulkhead(bulkHeadConfig.get());
        this.threadPoolBulkheadMap.put(bulkHeadConfig.get().getId(), threadPoolBulkhead);
    }

}
