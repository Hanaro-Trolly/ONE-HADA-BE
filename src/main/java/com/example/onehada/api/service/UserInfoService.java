package com.example.onehada.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.UserInfoDTO;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.exception.ForbiddenException;
import com.example.onehada.exception.UnauthorizedException;

@Service
public class UserInfoService {

	private final JwtService jwtService;
	private final UserRepository userRepository;


	public UserInfoService(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	public String getEmailFromToken(String accessToken) {
		return jwtService.extractEmail(accessToken);
	}


	public Optional<User> getUserEmail(String email) {
		return userRepository.findByUserEmail(email);
	}

	public Optional<User> getUserId(String token, int userId) {
		if (!jwtService.isValidToken(token)) {
			throw new UnauthorizedException("인증이 필요합니다.");
		}
		return userRepository.findByUserId(userId);
	}

	public void checkAccessToken(String token, String email) {
		String accessToken = token.replace("Bearer ", "");
		if (!jwtService.isTokenValid(accessToken, email)) {
			throw new ForbiddenException("접근권한이 없습니다.");
		}
	}

	public UserInfoDTO getUserInfo(String token, String email)
	{
		checkAccessToken(token, email);
		User user = userRepository.findByUserEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));
		System.out.println("User: " + user);
		return new UserInfoDTO(
			user.getUserId(),
			user.getUserName(),
			user.getUserEmail(),
			user.getPhoneNumber(),
			user.getUserAddress(),
			user.getUserBirth(),
			user.getUserRegisteredDate(),
			user.getUserGender()
		);

	}
}
