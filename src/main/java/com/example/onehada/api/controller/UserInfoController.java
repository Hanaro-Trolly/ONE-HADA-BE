package com.example.onehada.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.UserInfoService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.UserInfoDTO;
import com.example.onehada.db.dto.UserUpdateDTO;
import com.example.onehada.db.entity.User;


@RestController
@RequestMapping("/api/users")
public class UserInfoController {
	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getUser(@PathVariable int userId, @RequestHeader("Authorization") String token) {
			User user = userInfoService.getUserId(token, userId);
			UserInfoDTO userInfo = userInfoService.getUserInfo(user.getUserEmail());

			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 조회 성공", userInfo));
	}

	@PatchMapping("/{user_id}")
	public ResponseEntity<?> updateUser(@PathVariable int user_id, @RequestHeader("Authorization") String token,
		@RequestBody UserUpdateDTO infoUpdate) {

			userInfoService.getUserId(token, user_id);
			UserUpdateDTO userInfo = userInfoService.updateUser(user_id, infoUpdate);
			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 수정 성공", userInfo));
	}
}
