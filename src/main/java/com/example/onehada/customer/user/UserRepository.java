package com.example.onehada.customer.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.customer.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserEmail(String email);
	Optional<User> findByUserId(Long userId);
	List<User> findByUserNameContaining(String userName);
	List<User> findByUserBirth(String userBirth);
	List<User> findByUserNameContainingAndUserBirth(String userName, String userBirth);
	Optional<User> findByUserGoogleId(String userGoogleId);
	Optional<User> findByUserKakaoId(String userKakaoId);
	Optional<User> findByUserNaverId(String userNaverId);

	// 전화번호 검색을 위한 새로운 메서드들 추가
	List<User> findByPhoneNumber(String phoneNumber);
	List<User> findByUserNameContainingAndPhoneNumber(String userName, String phoneNumber);
	List<User> findByUserBirthAndPhoneNumber(String userBirth, String phoneNumber);
	List<User> findByUserNameContainingAndUserBirthAndPhoneNumber(String userName, String userBirth, String userPhone);
}
