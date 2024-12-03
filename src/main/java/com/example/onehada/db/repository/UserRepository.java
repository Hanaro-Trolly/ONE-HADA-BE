package com.example.onehada.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserEmail(String email);

	Optional<User> findByUserId(Long userId);

}
