package com.example.onehada.db.repository;

import com.example.onehada.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getUserEmail(), user);
    }

    public Optional<User> findByUserEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }
}
