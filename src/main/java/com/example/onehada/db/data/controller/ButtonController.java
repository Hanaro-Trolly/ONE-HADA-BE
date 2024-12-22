package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.repository.ButtonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buttons")
public class ButtonController {

    @Autowired
    private ButtonRepository buttonRepository;

    // 모든 사용자 조회
    @GetMapping
    public List<Button> getAllUsers() {
        return buttonRepository.findAll();
    }

    // 새 사용자 추가
    @PostMapping
    public Button addButton(@RequestBody Button button) {
        return buttonRepository.save(button);
    }
}

