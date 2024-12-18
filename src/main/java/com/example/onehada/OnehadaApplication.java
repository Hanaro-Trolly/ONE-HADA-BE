package com.example.onehada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement

public class OnehadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnehadaApplication.class, args);
	}

}
