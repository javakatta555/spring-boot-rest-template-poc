package com.example.repository;

import com.example.model.BulkHeadConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BulkHeadRepository extends MongoRepository<BulkHeadConfig,String> {
}
