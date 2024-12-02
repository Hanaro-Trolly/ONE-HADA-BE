package com.example.onehada.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserEmail(String email);
	List<User> findByUserNameContaining(String userName);
	List<User> findByUserBirth(String userBirth);
	List<User> findByUserNameContainingAndUserBirth(String userName, String userBirth);
}
