package com.example.onehada.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;

@RestController
public class HomeController {
	@Autowired
	UserRepository userRepository;
	@GetMapping("/")
	public String home() {
		User testUser = User.builder()
			.userEmail("test@tes.com")
			.userName("테스트")
			.simplePassword("1234")
			// 필요한 경우 다른 필수 필드들도 설정
			.userGender("M")
			.phoneNumber("01012345678")
			.userBirth("19900101")
			.build();

		userRepository.save(testUser);
		System.out.println("HomeController.home"+testUser.getUserEmail());

		return "One, hada!";
	}
}
