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

import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

	private final HistoryService historyService;

	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse> getHistories(@RequestHeader("Authorization") String token) {
		List<HistoryDTO> histories = historyService.getUserHistories(token);

		return ResponseEntity.ok(new ApiResponse(200, "OK", "활동내역 조회 성공", Map.of("histories", histories)));
	}

	@GetMapping("/{history_id}")
	public ResponseEntity<ApiResponse> getHistory(@RequestHeader("Authorization") String token, @PathVariable(
		"history_id") Long historyId) {
		HistoryDTO historyDTO = historyService.getUserHistory(historyId, token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "활동내역 상세 조회 성공", historyDTO));
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse> createHistory(@RequestHeader("Authorization") String token, @RequestBody HistoryDTO history) {
		HistoryDTO historyDTO = historyService.createHistory(history, token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "활동내역 등록 성공", Map.of("historyId",
			historyDTO.getHistoryId())));
	}
}
