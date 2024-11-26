package com.example.onehada.db.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

// MongoDB에서 User 데이터를 처리하는 리포지토리
public interface UserRepositori extends MongoRepository<User, String> {
}
