package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.ButtonNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ButtonNodeRepository extends Neo4jRepository<ButtonNode, String> {
}
