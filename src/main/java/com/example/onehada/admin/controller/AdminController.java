package com.example.onehada.admin.controller;

import com.example.onehada.admin.dto.AdminLoginRequestDTO;
import com.example.onehada.admin.dto.AdminLoginResponseDTO;
import com.example.onehada.admin.dto.ConsultationCreateRequestDTO;
import com.example.onehada.admin.dto.UserSearchRequestDTO;
import com.example.onehada.admin.service.AdminService;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 관련 API")
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;

	@Operation(summary = "관리자 로그인", description = "관리자 이메일과 비밀번호로 로그인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO request) throws BadRequestException {
		AdminLoginResponseDTO response = adminService.login(request);
		return ResponseEntity.ok(new ApiResult(200, "OK", "로그인 성공", response));
	}

	@Operation(summary = "상담원 정보 조회", description = "이메일 또는 이름으로 상담원을 검색합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공")
	})
	@GetMapping("/agent")
	public ResponseEntity<?> getAgents(
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name
	) {
		return ResponseEntity.ok(new ApiResult(
			200, "OK", "상담원 정보 조회 성공",
			adminService.getAgents(email, name)
		));
	}


	@Operation(summary = "상담 데이터 생성", description = "새로운 상담 데이터를 추가합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "404", description = "사용자 또는 상담원을 찾을 수 없음")
	})
	@PostMapping("/consultation")
	public ResponseEntity<?> createConsultation(@RequestBody ConsultationCreateRequestDTO request) throws
		NotFoundException {
		return ResponseEntity.ok(new ApiResult(
			201, "CREATED", "상담 데이터 추가 성공",
			adminService.createConsultation(request)
		));
	}

	@Operation(summary = "상담 내역 조회", description = "특정 사용자의 상담 내역을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공")
	})
	@GetMapping("/consultation/{userId}")
	public ResponseEntity<?> getConsultations(@PathVariable Long userId) {
		return ResponseEntity.ok(new ApiResult(
			200, "OK", "상담 데이터 조회 성공",
			adminService.getConsultations(userId)
		));
	}

	@Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공")
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		return ResponseEntity.ok(new ApiResult(
			200, "OK", "사용자 정보 조회 성공",
			adminService.getUser(userId)
		));
	}

	@Operation(summary = "사용자 검색", description = "이름, 생년월일, 전화번호로 사용자를 검색합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "검색 성공"),
		@ApiResponse(responseCode = "400", description = "검색 조건 누락")
	})
	@PostMapping("/user/search")
	public ResponseEntity<?> searchUsers(@RequestBody UserSearchRequestDTO request) {
		if (request.getUserName() == null && request.getUserBirth() == null && request.getUserPhone() == null) {
			return ResponseEntity.badRequest()
				.body(new ApiResult(400, "BAD_REQUEST", "검색 조건을 입력해주세요.", null));
		}

		return ResponseEntity.ok(new ApiResult(
			200, "OK", "사용자 검색 성공",
			adminService.searchUsers(request.getUserName(), request.getUserBirth(), request.getUserPhone())
		));
	}

	@Operation(summary = "상담사별 상담 내역 조회", description = "특정 상담사의 모든 상담 내역을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공")
	})
	@GetMapping("/consultationList/{agentId}")
	public ResponseEntity<?> getConsultationList(@PathVariable Long agentId) {
		return ResponseEntity.ok(new ApiResult(
			200, "OK", "상담사 상담 내역 조회 성공",
			adminService.getConsultationList(agentId)
		));
	}

}
