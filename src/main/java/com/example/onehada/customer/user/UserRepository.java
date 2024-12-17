package com.example.onehada.customer.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.customer.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	// 기존 메서드들
	Optional<User> findByUserEmail(String email);
	Optional<User> findByUserId(Long userId);
	List<User> findByUserNameContaining(String userName);
	List<User> findByUserBirth(String userBirth);
	List<User> findByUserNameContainingAndUserBirth(String userName, String userBirth);

	// 소셜 로그인을 위한 메서드들 추가
	// Entity의 필드명과 정확히 일치하도록 수정
	Optional<User> findByUserGoogleId(String userGoogleId);
	Optional<User> findByUserKakaoId(String userKakaoId);
	Optional<User> findByUserNaverId(String userNaverId);
}
