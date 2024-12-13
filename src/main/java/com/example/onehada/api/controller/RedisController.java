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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.RedisService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.TransactionDTO;

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

	@PostMapping("/transfer")
	public ResponseEntity<ApiResponse> getValidationValue(@RequestBody List<String> keys) {
		Map<String, String> result = new HashMap<>();

		try {
			for (String key : keys) {
				String value = redisService.getValue(key);
				result.put(key, value != null ? value : "No value found");
			}

			return ResponseEntity.ok(new ApiResponse(
				200,
				"success",
				"<Redis> 단일 정보 조회 -> 응답을 정상적으로 수행했습니다.",
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

	@PostMapping("/transfer")
	public ResponseEntity<ApiResponse> saveTransferDetails(@RequestBody Map<String, String> transferRequest) {
		try {
			Map<String, String> transferDetails = new HashMap<>();

			for (Map.Entry<String, String> entry : transferRequest.entrySet()) {
				transferDetails.put(entry.getKey(), entry.getValue());
				redisService.saveValue(entry.getKey(), entry.getValue());
			}

			ApiResponse response = new ApiResponse(
				200,
				"success",
				"<Redis> 계좌 이체 필요정보 -> 정상적으로 수행했습니다.",
				transferDetails
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse(
				500,
				"error",
				"계좌 이체 정보를 저장하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
