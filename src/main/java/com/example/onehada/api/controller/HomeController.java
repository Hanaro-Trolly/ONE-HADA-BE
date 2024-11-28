package com.example.onehada.api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;

import jakarta.transaction.Transactional;

@RestController
@Transactional
public class HomeController {
	private final UserRepository userRepository;

	@Autowired
	public HomeController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/")
	public String home() {

		return "One, hada!";
	}
}
