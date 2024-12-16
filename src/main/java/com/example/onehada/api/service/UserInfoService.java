package com.example.onehada.api.service;

import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.UserInfoDTO;
import com.example.onehada.db.dto.UserUpdateDTO;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.ForbiddenException;
import com.example.onehada.exception.NotFoundException;
import com.example.onehada.exception.UnauthorizedException;

import jakarta.transaction.Transactional;

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

	public User getUserId(String token, Long userId) {
		validateToken(token);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 사용자 입니다."));
		checkAccessToken(token, user.getUserEmail());
		return user;
	}

	private void validateToken(String token) {
		if(!jwtService.isValidToken(token)) {
			throw new UnauthorizedException("인증이 필요합니다.");
		}
	}

	private void checkAccessToken(String token, String email) {
		String accessToken = token.replace("Bearer ", "");
		String currentUserEmail = getEmailFromToken(accessToken);
		if (!currentUserEmail.equals(email)) {
			throw new ForbiddenException("접근권한이 없습니다.");
		}
	}

	public UserInfoDTO getUserInfo(String email)
	{
		User user = userRepository.findByUserEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));
		System.out.println("User: " + user);
		return convertToUserInfoDTO(user);
	}

	private UserInfoDTO convertToUserInfoDTO(User user) {
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

	@Transactional
	public UserUpdateDTO updateUser(Long userId, UserUpdateDTO userUpdate) {

		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		String updateUserPhone = userUpdate.getUserPhone();
		String updateUserAddress = userUpdate.getUserAddress();
		if (updateUserPhone.isEmpty() && updateUserAddress.isEmpty()) {
			throw new BadRequestException("잘못된 형식의 데이터 입니다.");
		}
		if (!updateUserPhone.isEmpty()) {
			user.setPhoneNumber(updateUserPhone);
		}
		if (!updateUserAddress.isEmpty()) {
			user.setUserAddress(updateUserAddress);
		}

		userRepository.save(user);
		return new UserUpdateDTO(user.getPhoneNumber(), user.getUserAddress());
	}
}
