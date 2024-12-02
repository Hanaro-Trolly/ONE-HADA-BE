package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.ProductNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ProductRepository extends Neo4jRepository<ProductNode, String> {
}
