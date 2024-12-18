package com.example.onehada.db.data.repository;

import com.example.onehada.db.data.ProductNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface ProductNodeRepository extends Neo4jRepository<ProductNode, String> {
    @Query("""
                MATCH (b:Button {name:$buttonName})-[r:Recommend]->(p:Product)
                               RETURN p
                               ORDER BY r.weight DESC LIMIT 3
            """)
    List<ProductNode> findTop3RecommendedProductsByButton(String buttonName);

}
