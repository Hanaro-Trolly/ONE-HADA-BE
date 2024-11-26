package com.example.onehada.db.repository;

import com.example.onehada.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    // 테스트용 사용자 추가
    public UserRepository() {
        users.put("test@test.com", User.builder()
            .userEmail("test@test.com")
            .userName("테스트")
            .simplePassword("1234")
            .build());
    }

    public Optional<User> findByUserEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }
}
