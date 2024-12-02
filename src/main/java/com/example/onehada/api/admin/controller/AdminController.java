package com.example.onehada.api.admin.controller;

import com.example.onehada.api.admin.dto.*;
import com.example.onehada.api.admin.service.AdminService;
import com.example.onehada.db.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO request) {
		try {
			AdminLoginResponseDTO response = adminService.login(request);
			return ResponseEntity.ok(new ApiResponse(200, "OK", "로그인 성공", response));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "이메일 또는 비밀번호가 잘못되었습니다.", null));
		}
	}

	@GetMapping("/agent")
	public ResponseEntity<?> getAgents(
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name
	) {
		try {
			return ResponseEntity.ok(new ApiResponse(
				200, "OK", "상담원 정보 조회 성공",
				adminService.getAgents(email, name)
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "상담원 정보 조회 실패", null));
		}
	}

	@GetMapping("/activity_logs/{userId}")
	public ResponseEntity<?> getActivityLogs(@PathVariable String userId) {
		try {
			return ResponseEntity.ok(new ApiResponse(
				200, "OK", "활동 로그 조회 성공",
				adminService.getActivityLogs(userId)
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "활동 로그 조회 실패", null));
		}
	}

	@PostMapping("/consultation")
	public ResponseEntity<?> createConsultation(@RequestBody ConsultationCreateRequestDTO request) {
		try {
			return ResponseEntity.ok(new ApiResponse(
				201, "CREATED", "상담 데이터 추가 성공",
				adminService.createConsultation(request)
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "상담 데이터 추가 실패", null));
		}
	}

	@GetMapping("/consultation/{userId}")
	public ResponseEntity<?> getConsultations(@PathVariable String userId) {
		try {
			return ResponseEntity.ok(new ApiResponse(
				200, "OK", "상담 데이터 조회 성공",
				adminService.getConsultations(userId)
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "상담 데이터 조회 실패", null));
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUser(@PathVariable String userId) {
		try {
			return ResponseEntity.ok(new ApiResponse(
				200, "OK", "사용자 정보 조회 성공",
				adminService.getUser(userId)
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "사용자 정보 조회 실패", null));
		}
	}
}
