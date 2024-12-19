package com.example.onehada.db.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.neo4j.driver.Driver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class AppConfig {
    // @Bean
    // public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    //     return new JpaTransactionManager(entityManagerFactory);
    // }

    // @Bean
    // public PlatformTransactionManager transactionManager(Driver driver) {
    //     return new Neo4jTransactionManager(driver);
    // }
}
