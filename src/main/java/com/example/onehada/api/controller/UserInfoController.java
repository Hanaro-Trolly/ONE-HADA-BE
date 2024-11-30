package com.example.onehada.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.UserInfoService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.UserInfoDTO;
import com.example.onehada.db.entity.User;
import com.example.onehada.exception.ForbiddenException;
import com.example.onehada.exception.NotFoundException;
import com.example.onehada.exception.UnauthorizedException;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {
	private final UserInfoService userInfoService;

	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getUser(@PathVariable int userId, @RequestHeader("Authorization") String token) {
		try {
			User user = userInfoService.getUserId(token, userId).orElseThrow(() -> new NotFoundException("존재하지 않는 "
				+ "사용자 입니다."));

			UserInfoDTO userInfo = userInfoService.getUserInfo(token, user.getUserEmail());

			return ResponseEntity.ok(new ApiResponse(200, "OK", "사용자 정보 조회 성공", userInfo));
		} catch (UnauthorizedException e) {
			return ResponseEntity.badRequest().body(new ApiResponse(401, "UNAUTHORIZED", e.getMessage(), null));
		} catch (ForbiddenException e){
			return ResponseEntity.badRequest().body(new ApiResponse(403, "FORBIDDEN", e.getMessage(), null));

		}catch (NotFoundException e) {
			return ResponseEntity.badRequest().body(new ApiResponse(404, "NOT_FOUND", e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", e.getMessage() +
				"서버에러",
				null));
		}

	}

}
