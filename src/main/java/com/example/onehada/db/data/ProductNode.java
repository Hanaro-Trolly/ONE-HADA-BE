package com.example.onehada.db.data;

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


    // 기본 생성자
    public ProductNode() {}

    @Relationship(type = "Recommendation", direction = Relationship.Direction.OUTGOING)
    private Set<ProductNode> recommendproduct;
    // 생성자
    public ProductNode(String name) {
        this.name = name;
    }

    // Getter와 Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

