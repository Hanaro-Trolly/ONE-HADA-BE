package com.example.onehada.customer.history;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "History", description = "활동 내역 관련 API")
@RequestMapping("/api/history")
public class HistoryController {

	private final HistoryService historyService;

	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Operation(summary = "활동 내역 목록 조회", description = "사용자의 전체 활동 내역을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("")
	public ResponseEntity<ApiResult> getHistories(@RequestHeader("Authorization") String token) {
		List<HistoryDTO> histories = historyService.getUserHistories(token);

		return ResponseEntity.ok(new ApiResult(200, "OK", "활동내역 조회 성공", Map.of("histories", histories)));
	}

	@Operation(summary = "활동 내역 상세 조회", description = "특정 활동 내역의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "활동 내역을 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/{history_id}")
	public ResponseEntity<ApiResult> getHistory(@RequestHeader("Authorization") String token, @PathVariable(
		"history_id") Long historyId) {
		HistoryDTO historyDTO = historyService.getUserHistory(historyId, token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "활동내역 상세 조회 성공", historyDTO));

	}

	@Operation(summary = "활동 내역 생성", description = "새로운 활동 내역을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("")
	public ResponseEntity<ApiResult> createHistory(@RequestHeader("Authorization") String token, @RequestBody HistoryDTO history) {
		HistoryDTO historyDTO = historyService.createHistory(history, token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "활동내역 등록 성공", Map.of("historyId",
			historyDTO.getHistoryId())));
	}
}
