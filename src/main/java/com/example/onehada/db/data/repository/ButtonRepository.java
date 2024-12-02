package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.Button;
import org.springframework.data.mongodb.repository.MongoRepository;

// MongoDB에서 User 데이터를 처리하는 리포지토리
public interface ButtonRepository extends MongoRepository<Button, String> {
}
