package com.example.onehada.db.mongodb;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") // MongoDB 컬렉션 이름
public class User {

    @Id
    private String id; // MongoDB 기본 키
    private String name;
    private String email;

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String name, String email) {
        this.name = name;
        this.email = email;
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
