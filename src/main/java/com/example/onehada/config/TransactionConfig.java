package com.example.onehada.config;

import jakarta.persistence.EntityManagerFactory;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    @Qualifier("neo4jTransactionManager")
    @Bean
    public PlatformTransactionManager neo4jtransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}
