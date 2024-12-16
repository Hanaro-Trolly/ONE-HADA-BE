package com.example.onehada.customer.user;

import org.springframework.stereotype.Service;

import com.example.onehada.exception.user.UserNotFoundException;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getUserByEmail(String email) {
		return userRepository.findByUserEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당 email로 유저를 찾을 수 없습니다. Email: " + email));
	}
}
