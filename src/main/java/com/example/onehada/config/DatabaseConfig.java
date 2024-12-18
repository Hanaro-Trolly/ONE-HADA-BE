//package com.example.onehada.config;
//
//
//import com.mongodb.client.MongoClient;
//import org.neo4j.driver.Driver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//import org.springframework.data.neo4j.core.Neo4jClient;
//import org.springframework.data.neo4j.core.Neo4jTemplate;
//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
//
//@Configuration
//@EnableJpaRepositories(basePackages = "com.example.onehada.customer")
//@EnableMongoRepositories(basePackages = "com.example.onehada.db.data.repository")
//@EnableNeo4jRepositories(basePackages = "com.example.onehada.db.data.repository")
//public class DatabaseConfig {
//
//    @Bean
//    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
//        return new MongoTemplate(mongoClient, "onehada");
//    }
//
//    @Bean
//    public Neo4jTemplate neo4jTemplate(Neo4jClient driver) {
//        return new Neo4jTemplate(driver);
//    }
//}