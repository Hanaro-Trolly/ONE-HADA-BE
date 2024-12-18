package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.Button;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

// MongoDB에서 User 데이터를 처리하는 리포지토리
public interface ButtonRepository extends MongoRepository<Button, String> {
    @Aggregation(pipeline = {
        "{ $match: { 'userId': ?0 } }",
        "{ $group: { _id: '$buttonId', count: { $sum: 1 } } }",
        "{ $sort: { count: -1 } }",
        "{ $limit: 1 }",
        "{ $project: { _id: 1 } }"
    })
    String findMostClickedButtonByUserId(String userId);
}
