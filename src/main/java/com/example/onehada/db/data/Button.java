package com.example.onehada.db.data;
import lombok.Builder;
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

    public Button(String name, String type){
        this.name = name;
        this.type = type;
    }

}
