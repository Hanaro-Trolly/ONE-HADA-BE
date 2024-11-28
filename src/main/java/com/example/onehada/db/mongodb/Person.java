package com.example.onehada.db.mongodb;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
@Data
@Node("Person")  // Neo4j의 노드 타입
public class Person {

    @Id
    private String name;
    private int age;

    // 기본 생성자
    public Person() {}

    @Relationship(type = "FRIENDS_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<Person> friends;
    // 생성자
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getter와 Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
