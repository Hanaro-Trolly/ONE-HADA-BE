//package com.example.onehada.db.data;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
//import org.neo4j.driver.Driver;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//public class AppConfig {
//
//    @Bean(name = "transactionManagerNeo4j")
//    public PlatformTransactionManager transactionManager1(Driver driver) {
//        return new Neo4jTransactionManager(driver);
//    }
//
////    @Bean
////    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
////        return new JpaTransactionManager(emf);
////    }
//}
