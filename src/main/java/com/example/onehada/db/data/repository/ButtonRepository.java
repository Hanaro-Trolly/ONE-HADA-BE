package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonIdDTO;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

// MongoDB에서 User 데이터를 처리하는 리포지토리
public interface ButtonRepository extends MongoRepository<Button, String> {
    // @Aggregation(pipeline = {
    //     "{ $match: { 'userId': ?0 } }",
    //     "{ $group: { _id: '$buttonId', count: { $sum: 1 } } }",
    //     "{ $sort: { count: -1 } }",
    //     "{ $limit: 1 }",
    //     "{ $project: { buttonId: '$_id', _id: 0 } }"
    // })
    // ButtonIdDTO findMostClickedButtonByUserId(String userId);

    Button findByName(String name);
}
