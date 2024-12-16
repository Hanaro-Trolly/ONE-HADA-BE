package com.example.onehada.db.data;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "buttons") // MongoDB 컬렉션 이름
public class Button {

    @Id
    private String id; // MongoDB 기본 키
    private String name; // button name
    private String type;


    // 기본 생성자


}
//