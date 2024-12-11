package com.example.onehada.api.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.RedisService;
import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

	@Autowired
	private RedisService redisService;

	@PostMapping("/set")
	public ResponseEntity<ApiResponse> setValue(@RequestParam("key") String key, @RequestParam("value") String value) {
		try {
			redisService.saveValue(key, value);

			return ResponseEntity.ok(new ApiResponse(
				200,
				"success",
				"Value saved successfully!",
				Map.of("key", key, "value", value)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
				500,
				"error",
				"Failed to save value: " + e.getMessage(),
				null));
		}
	}

	@GetMapping("/get")
	public ResponseEntity<ApiResponse> getValue(@RequestParam("key") String key) {
		try {
			String value = redisService.getValue(key);
			if (value != null) {
				return ResponseEntity.ok(new ApiResponse(
					200,
					"success",
					"Value retrieved successfully!",
					Map.of("key", key, "value", value)
				));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(
					404,
					"error",
					"No value found for the given key!",
					null
				));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
				500,
				"error",
				"Failed to retrieve value: " + e.getMessage(),
				null
			));
		}
	}

	@GetMapping("/get/transfer")
	public ResponseEntity<ApiResponse> getValidationValue() {
		List<String> keys = Arrays.asList("myaccount", "receiveaccount", "amount");

		Map<String, String> result = new HashMap<>();

		try {
			for (String key : keys) {
				String value = redisService.getValue(key);
				result.put(key, value != null ? value : "No value found");
			}

			return ResponseEntity.ok(new ApiResponse(
				200,
				"success",
				"<Redis> 단일 정보 저장 -> 응답을 정상적으로 수행했습니다.",
				result
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
				500,
				"INTERNAL_SERVER_ERROR",
				"Failed to retrieve values: " + e.getMessage(),
				null
			));
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteValue(@RequestParam("key") String key) {
		try {
			boolean isDeleted = redisService.deleteValue(key);

			if (isDeleted) {
				return ResponseEntity.ok(new ApiResponse(
					200,
					"success",
					"Value deleted successfully!",
					Map.of("key", key)
				));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(
					404,
					"error",
					"No value found for the given key!",
					null // 데이터 없음
				));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
				500,
				"error",
				"Failed to delete value: " + e.getMessage(),
				null // 데이터 없음
			));
		}
	}
}
