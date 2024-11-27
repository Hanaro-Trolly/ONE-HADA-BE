package com.example.onehada.db.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepositoro userRepositoro;

    // 모든 사용자 조회
    @GetMapping
    public List<User> getAllUsers() {
        return userRepositoro.findAll();
    }

    // 새 사용자 추가
    @PostMapping
    public User addUser(@RequestBody User user) {
        return userRepositoro.save(user);
    }
}

