package com.example.onehada.db.data.controller;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.service.ButtonService;
import com.example.onehada.db.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/button")
public class ButtonNodeController {
    
    private final ButtonService buttonService;
    private final JwtService jwtService;

    public ButtonNodeController(ButtonService buttonService, JwtService jwtService) {
        this.buttonService = buttonService;
        this.jwtService = jwtService;
    }

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ButtonSession> getUserSessions(@PathVariable String userId) {
        ButtonSession sessions = buttonService.processUserClickHistory(userId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{buttonId}")
    public ResponseEntity<ApiResponse> logButtonClick(@RequestHeader("Authorization") String token,@PathVariable String buttonId) {
        String accessToken = token.replace("Bearer ", "");
        String userId = jwtService.extractUserId(accessToken).toString();
        buttonService.saveButtonLog(userId, buttonId);
        Button button = buttonService.getButtonById(buttonId);

        if (button != null && "product".equals(button.getType())) {
            buttonService.processUserClickHistory(userId);
        }

        return ResponseEntity.ok(new ApiResponse(200, "OK", "버튼 클릭 로그 저장 성공", null));
    }
}
