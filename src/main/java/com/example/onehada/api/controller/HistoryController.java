package com.example.onehada.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.HistoryService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.HistoryDTO;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

	private final HistoryService historyService;

	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse> getHistory(@RequestHeader("Authorization") String token) {
		List<HistoryDTO> histories = historyService.getUserHistories(token);

		return ResponseEntity.ok(new ApiResponse(200, "OK", "활동내역 조회 성공", Map.of("histories", histories)));
	}
}
