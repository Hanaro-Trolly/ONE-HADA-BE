package com.example.onehada.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.ConsultationService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.ConsultationDTO;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

	private final ConsultationService consultationService;

	public ConsultationController(ConsultationService consultationService) {
		this.consultationService = consultationService;
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse> getConsultations(@RequestHeader("Authorization") String token) {
		List<ConsultationDTO> consultations = consultationService.getConsultations(token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "상담내역 조회 성공", consultations));
	}
}
