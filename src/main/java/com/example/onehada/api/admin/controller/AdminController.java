package com.example.onehada.api.admin.controller;

import com.example.onehada.api.admin.dto.*;
import com.example.onehada.api.admin.exception.AgentNotFoundException;
import com.example.onehada.api.admin.exception.InvalidCredentialsException;
import com.example.onehada.api.admin.exception.UserNotFoundException;
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
	public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO request) throws InvalidCredentialsException {
		AdminLoginResponseDTO response = adminService.login(request);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "로그인 성공", response));
	}

	@GetMapping("/agent")
	public ResponseEntity<?> getAgents(
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name
	) {
		return ResponseEntity.ok(new ApiResponse(
			200, "OK", "상담원 정보 조회 성공",
			adminService.getAgents(email, name)
		));
	}

	@GetMapping("/activity_logs/{userId}")
	public ResponseEntity<?> getActivityLogs(@PathVariable Long userId) throws UserNotFoundException {
		return ResponseEntity.ok(new ApiResponse(
			200, "OK", "활동 로그 조회 성공",
			adminService.getActivityLogs(userId)
		));
	}

	@PostMapping("/consultation")
	public ResponseEntity<?> createConsultation(@RequestBody ConsultationCreateRequestDTO request) throws
		UserNotFoundException,
		AgentNotFoundException {
		return ResponseEntity.ok(new ApiResponse(
			201, "CREATED", "상담 데이터 추가 성공",
			adminService.createConsultation(request)
		));
	}

	@GetMapping("/consultation/{userId}")
	public ResponseEntity<?> getConsultations(@PathVariable Long userId) {
		return ResponseEntity.ok(new ApiResponse(
			200, "OK", "상담 데이터 조회 성공",
			adminService.getConsultations(userId)
		));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		return ResponseEntity.ok(new ApiResponse(
			200, "OK", "사용자 정보 조회 성공",
			adminService.getUser(userId)
		));
	}

	@GetMapping("/user/search")
	public ResponseEntity<?> searchUsers(
		@RequestParam(required = false) String user_name,
		@RequestParam(required = false) String user_birth
	) {
		if (user_name == null && user_birth == null) {
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "검색 조건을 입력해주세요.", null));
		}

		return ResponseEntity.ok(new ApiResponse(
			200, "OK", "사용자 검색 성공",
			adminService.searchUsers(user_name, user_birth)
		));
	}
}
