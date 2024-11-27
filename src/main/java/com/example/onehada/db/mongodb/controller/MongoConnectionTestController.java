package com.example.onehada.db.mongodb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.bson.Document;

@RestController
@RequestMapping("/api/mongo-test")
public class MongoConnectionTestController {

	@Autowired
	private MongoTemplate mongoTemplate;

	@GetMapping("/connection")
	public ResponseEntity<String> testConnection() {
		try {
			// 데이터베이스 이름 가져오기 시도
			String dbName = mongoTemplate.getDb().getName();
			return ResponseEntity.ok("MongoDB Connection Successful! Connected to database: " + dbName);
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
				.body("MongoDB Connection Failed: " + e.getMessage());
		}
	}

	@GetMapping("/ping")
	public ResponseEntity<String> pingMongo() {
		try {
			// Bson Document 객체 생성하여 ping 명령 실행
			Document pingCommand = new Document("ping", 1);
			mongoTemplate.getDb().runCommand(pingCommand);
			return ResponseEntity.ok("MongoDB Ping Successful!");
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
				.body("MongoDB Ping Failed: " + e.getMessage());
		}
	}
}
