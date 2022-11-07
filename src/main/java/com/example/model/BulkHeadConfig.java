package com.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "BulkHeadConfig")
public class BulkHeadConfig {

    @Id
    private String id;
    private int queueCapacity;
    private int keepAliveDuration;
    private int maxThreadPool;
    private boolean isActive;

}
