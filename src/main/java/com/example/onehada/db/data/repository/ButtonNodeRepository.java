package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.ButtonNode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ButtonNodeRepository extends Neo4jRepository<ButtonNode, String> {
    @Query("""
        MATCH (b:Button {name: $buttonName})
        MATCH (p:Product {name: $productName})
        WITH b, p
        OPTIONAL MATCH (b)-[r:Recommend]->(p)
        WHERE r IS NULL OR r.lastUpdated < $currentTime
        MERGE (b)-[newR:Recommend]->(p)
        ON CREATE SET newR.weight = 1, newR.lastUpdated = $currentTime
        ON MATCH SET newR.weight = newR.weight + 1, newR.lastUpdated = $currentTime
        RETURN newR
    """)
    void incrementRecommendationWeight(
            @Param("buttonName") String buttonName,
            @Param("productName") String productName,
            @Param("currentTime") LocalDateTime currentTime
    );
}