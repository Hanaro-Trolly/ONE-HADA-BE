package com.example.onehada.customer.consultation;

import java.util.List;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "Consultation", description = "상담 내역 관련 API")
@RequestMapping("/api/consultations")
public class ConsultationController {

	private final ConsultationService consultationService;

	public ConsultationController(ConsultationService consultationService) {
		this.consultationService = consultationService;
	}

	@Operation(summary = "상담 내역 조회", description = "현재 로그인한 사용자의 모든 상담 내역을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("")
	public ResponseEntity<ApiResult> getConsultations(@RequestHeader("Authorization") String token) {
		List<ConsultationDTO> consultations = consultationService.getConsultations(token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "상담내역 조회 성공", Map.of("consultations", consultations)));
	}
}
