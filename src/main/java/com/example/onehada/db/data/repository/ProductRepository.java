package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
