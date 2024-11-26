package com.example.onehada.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.RedisService;

@RestController
@RequestMapping("/api/redis-test")
public class RedisTestController {
	@Autowired
	private RedisService redisService;

	@GetMapping("/test")
	public ResponseEntity<String> testRedis() {
		try {
			String testKey = "test-key";
			String testValue = "test-value";

			// Redis에 값 저장
			redisService.saveValue(testKey, testValue);

			// Redis에서 값 조회
			String retrievedValue = redisService.getValue(testKey);

			if (testValue.equals(retrievedValue)) {
				return ResponseEntity.ok("Redis is working properly! Value: " + retrievedValue);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Redis test failed: Values don't match");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Redis test failed: " + e.getMessage());
		}
	}
}
