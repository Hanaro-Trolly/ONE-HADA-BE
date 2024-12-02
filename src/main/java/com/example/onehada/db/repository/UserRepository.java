package com.example.onehada.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserEmail(String email);
}
