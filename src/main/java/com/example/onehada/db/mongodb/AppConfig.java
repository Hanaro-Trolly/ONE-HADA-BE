package com.example.onehada.db.mongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.neo4j.driver.Driver;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AppConfig {

    @Bean
    public PlatformTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}
