package com.example.onehada.db.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepositori extends MongoRepository<User, String> {
}
