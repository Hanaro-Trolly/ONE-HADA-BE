package com.example.onehada.customer.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {
	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@GetMapping("")
	public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
			UserInfoDTO userInfo = userInfoService.getUserInfo(token);

			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 조회 성공", userInfo));
	}

	@PatchMapping("")
	public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
		@RequestBody UserUpdateDTO infoUpdate) {

			UserUpdateDTO userInfo = userInfoService.updateUser(token, infoUpdate);
			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 수정 성공", userInfo));
	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
		userInfoService.deleteUser(token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "사용사 탈퇴 완료", null));
	}
}
