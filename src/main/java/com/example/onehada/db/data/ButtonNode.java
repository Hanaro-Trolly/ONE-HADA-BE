package com.example.onehada.db.data;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Data
@Node("Button")  // Neo4j 노드 타입 지정
public class ButtonNode  {

    @Id
    private String name;

    // 버튼과 제품 간 관계 (예: 버튼 클릭이 특정 제품과 연관될 때)
    @Relationship(type = "Recommendation", direction = Relationship.Direction.OUTGOING)
    private Set<ButtonNode> recommendedProduct;

    public ButtonNode() {}

    public ButtonNode(  String name) {
        this.name = name;
    }


}
