package com.example.onehada.db.data;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@Node("Button")  // Neo4j 노드 타입 지정
public class ButtonNode {

    @Id
    private String id;
    private String name;
    private int clickpath;
    private String email;

    // 버튼과 제품 간 관계 (예: 버튼 클릭이 특정 제품과 연관될 때)
    @Relationship(type = "Recommendation", direction = Relationship.Direction.OUTGOING)
    private ProductNode recommendedProduct;

    public ButtonNode() {}

    public ButtonNode(String id, int clickpath, String email, String name) {
        this.id = id;
        this.clickpath = clickpath;
        this.email = email;
        this.name = name;
    }

    public void setRecommendededProduct(ProductNode relatedProduct) {
    }
}
