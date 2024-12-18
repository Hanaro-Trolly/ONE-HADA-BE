package com.example.onehada.db.data.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class Neo4jTestRunnerController implements CommandLineRunner {

    private final Neo4jClient neo4jClient;

    public Neo4jTestRunnerController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) throws Exception {
        neo4jClient.query("RETURN 'Neo4j Connection Successful' AS result")
                .fetchAs(String.class)
                .one()
                .ifPresent(System.out::println);
    }
}
