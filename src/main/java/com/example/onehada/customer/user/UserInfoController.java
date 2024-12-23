package com.example.onehada.customer.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@Tag(name = "User", description = "사용자 정보 관련 API")
@RequestMapping("/api/user")
public class UserInfoController {
	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("")
	public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
			UserInfoDTO userInfo = userInfoService.getUserInfo(token);

			return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 정보 조회 성공", userInfo));
	}

	@Operation(summary = "사용자 정보 수정", description = "사용자의 전화번호와 주소 정보를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@PatchMapping("")
	public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
		@RequestBody UserUpdateDTO infoUpdate) {

			UserUpdateDTO userInfo = userInfoService.updateUser(token, infoUpdate);
			return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 정보 수정 성공", userInfo));
	}

	@Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "탈퇴 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("")
	public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
		userInfoService.deleteUser(token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 탈퇴 완료", null));
	}
}
