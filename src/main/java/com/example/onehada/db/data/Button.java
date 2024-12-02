package com.example.onehada.db.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "buttons") // MongoDB 컬렉션 이름
public class Button {

    @Id
    private String id; // MongoDB 기본 키
    private String name; // product name

    public int getClickpath() {
        return clickpath;
    }

    public void setClickpath(int clickpath) {
        this.clickpath = clickpath;
    }

    private int clickpath;
    private String email;

    // 기본 생성자
    public Button() {}

    // 생성자
    public Button(String name, String email, int clickpath) {
        this.name = name;
        this.email = email;
        this.clickpath = clickpath;
    }



    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
//