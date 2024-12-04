package com.example.onehada.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.exception.user.UserNotFoundException;

@Service
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getUserByEmail(String email) {
		return userRepository.findByUserEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당 email로 유저를 찾을 수 없습니다. Email: " + email));
	}

	// 소셜 로그인을 위한 메서드들 추가
	public User getUserByGoogleId(String googleId) {
		return userRepository.findByUserGoogleId(googleId)
			.orElseThrow(() -> new UserNotFoundException("해당 Google ID로 유저를 찾을 수 없습니다."));
	}

	public User getUserByKakaoId(String kakaoId) {
		return userRepository.findByUserKakaoId(kakaoId)
			.orElseThrow(() -> new UserNotFoundException("해당 Kakao ID로 유저를 찾을 수 없습니다."));
	}

	public User getUserByNaverId(String naverId) {
		return userRepository.findByUserNaverId(naverId)
			.orElseThrow(() -> new UserNotFoundException("해당 Naver ID로 유저를 찾을 수 없습니다."));
	}
}
