package com.example.onehada.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Redis", description = "Redis 캐시 관련 API")
@RequestMapping("/api/redis")
public class RedisController {

	@Autowired
	private RedisService redisService;

	@Operation(summary = "Redis 값 조회", description = "Redis에 저장된 키에 대한 값을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("get")
	public ResponseEntity<ApiResult> getValidationValue(@RequestBody List<String> keys) {
		Map<String, String> result = new HashMap<>();

		try {
			for (String key : keys) {
				String value = redisService.getValue(key);
				result.put(key, value != null ? value : "No value found");
			}

			return ResponseEntity.ok(new ApiResult(
				200,
				"success",
				"<Redis> 단일 정보 조회 -> 응답을 정상적으로 수행했습니다.",
				result
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResult(
				500,
				"INTERNAL_SERVER_ERROR",
				"Failed to retrieve values: " + e.getMessage(),
				null
			));
		}
	}

	@Operation(summary = "이체 정보 저장", description = "계좌 이체에 필요한 정보를 Redis에 저장합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "저장 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping
	public ResponseEntity<ApiResult> saveTransferDetails(@RequestBody Map<String, String> transferRequest) {
		try {
			Map<String, String> transferDetails = new HashMap<>();

			for (Map.Entry<String, String> entry : transferRequest.entrySet()) {
				transferDetails.put(entry.getKey(), entry.getValue());
				redisService.saveValue(entry.getKey(), entry.getValue());
			}

			ApiResult response = new ApiResult(
				200,
				"success",
				"<Redis> 계좌 이체 필요정보 -> 정상적으로 수행했습니다.",
				transferDetails
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResult response = new ApiResult(
				500,
				"error",
				"계좌 이체 정보를 저장하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Operation(summary = "이체 정보 수정", description = "Redis에 저장된 이체 정보를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PatchMapping
	public ResponseEntity<ApiResult> updateTransferDetails(@RequestBody Map<String, String> transferRequest) {
		try {
			Map<String, String> transferDetails = new HashMap<>();

			for (Map.Entry<String, String> entry : transferRequest.entrySet()) {
				transferDetails.put(entry.getKey(), entry.getValue());
				redisService.saveValue(entry.getKey(), entry.getValue()); // 수정된 값 Redis에 저장
			}

			ApiResult response = new ApiResult(
				200,
				"success",
				"<Redis> 계좌 이체 필요정보 -> 수정이 정상적으로 수행되었습니다.",
				transferDetails
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResult response = new ApiResult(
				500,
				"error",
				"계좌 이체 정보를 수정하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Operation(summary = "이체 정보 삭제", description = "Redis에 저장된 이체 정보를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("delete")
	public ResponseEntity<ApiResult> deleteTransferDetails(@RequestBody List<String> keys) {
		try {
			for (String key : keys) {
				redisService.deleteValue(key);
			}

			ApiResult response = new ApiResult(
				200,
				"success",
				"<Redis> 계좌 이체 정보 삭제 -> 삭제가 정상적으로 수행되었습니다.",
				keys
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResult response = new ApiResult(
				500,
				"error",
				"계좌 이체 정보를 삭제하는 중 오류가 발생했습니다: " + e.getMessage(),
				null
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
