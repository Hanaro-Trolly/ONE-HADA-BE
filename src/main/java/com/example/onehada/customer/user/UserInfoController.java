package com.example.onehada.customer.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {
	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
			User user = userInfoService.getUserId(token, userId);
			UserInfoDTO userInfo = userInfoService.getUserInfo(user.getUserEmail());

			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 조회 성공", userInfo));
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestHeader("Authorization") String token,
		@RequestBody UserUpdateDTO infoUpdate) {

			userInfoService.getUserId(token, userId);
			UserUpdateDTO userInfo = userInfoService.updateUser(userId, infoUpdate);
			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 수정 성공", userInfo));
	}
}
