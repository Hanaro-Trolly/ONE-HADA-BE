package com.example.onehada.db.data.controller;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.service.ButtonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/button")
public class ButtonNodeController {
    
    private final ButtonService buttonService;

    public ButtonNodeController(ButtonService buttonService) {
        this.buttonService = buttonService;
    }

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ButtonSession> getUserSessions(@PathVariable String userId) {
        ButtonSession sessions = buttonService.processUserClickHistory(userId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/log")
    public ResponseEntity<?> logButtonClick(@RequestParam String userId, @RequestParam String buttonId) {
        buttonService.saveButtonLog(userId, buttonId);
        Button button = buttonService.getButtonById(buttonId);

        if ("product".equals(button.getType())) {
            buttonService.processUserClickHistory(userId);
        }

        return ResponseEntity.ok().build();
    }
}
