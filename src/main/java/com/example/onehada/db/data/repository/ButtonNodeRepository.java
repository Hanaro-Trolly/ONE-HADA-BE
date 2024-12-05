package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.ButtonNode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ButtonNodeRepository extends Neo4jRepository<ButtonNode, String> {
    @Query("""
        MATCH (b:Button {name: $buttonName})-[r:Recommend]->(p:Product {name: $productName})
        SET r.weight = COALESCE(r.weight, 0) + 1
        RETURN r
    """)
    void incrementRecommendationWeight(@Param("buttonName") String buttonName, @Param("productName") String productName);

}
