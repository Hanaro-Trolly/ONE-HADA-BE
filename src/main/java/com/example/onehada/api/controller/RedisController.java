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
import org.springframework.web.bind.annotation.PatchMapping;
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

	@GetMapping
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

	@PostMapping
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

	@PatchMapping
	public ResponseEntity<ApiResponse> updateTransferDetails(@RequestBody Map<String, String> transferRequest) {
		try {
			Map<String, String> transferDetails = new HashMap<>();

			for (Map.Entry<String, String> entry : transferRequest.entrySet()) {
				transferDetails.put(entry.getKey(), entry.getValue());
				redisService.saveValue(entry.getKey(), entry.getValue()); // 수정된 값 Redis에 저장
			}

			ApiResponse response = new ApiResponse(
				200,
				"success",
				"<Redis> 계좌 이체 필요정보 -> 수정이 정상적으로 수행되었습니다.",
				transferDetails
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse(
				500,
				"error",
				"계좌 이체 정보를 수정하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse> deleteTransferDetails(@RequestBody List<String> keys) {
		try {
			for (String key : keys) {
				redisService.deleteValue(key);
			}

			ApiResponse response = new ApiResponse(
				200,
				"success",
				"<Redis> 계좌 이체 정보 삭제 -> 삭제가 정상적으로 수행되었습니다.",
				keys
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse(
				500,
				"error",
				"계좌 이체 정보를 삭제하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
