package com.example.onehada.db.data;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "products")  // MongoDB의 컬렉션 이름
public class Product {

    @Id
    private String id;
    private String name;

    // 기본 생성자
    public Product() {}

    // 생성자
    public Product(String name) {
        this.name = name;
    }

    // Getter와 Setter는 Lombok @Data 어노테이션으로 대체됨
}
