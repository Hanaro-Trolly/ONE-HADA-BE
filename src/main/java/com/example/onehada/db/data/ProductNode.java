package com.example.onehada.db.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
@Data
@Node("Product")  // Neo4j의 노드 타입
public class ProductNode {

    @Id
    private String name;
    private Set<ButtonNode> recommendedByButtons;


    // 기본 생성자
    public ProductNode() {}

    @Relationship(type = "Recommend", direction = Relationship.Direction.INCOMING)
    @JsonIgnore
    private Set<ProductNode> recommendproduct;
    // 생성자
    public ProductNode(String name) {
        this.name = name;
    }

    public Set<ButtonNode> getRecommendedByButtons() {
        return recommendedByButtons;
    }

}

