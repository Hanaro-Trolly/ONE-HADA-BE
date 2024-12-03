package com.example.onehada.api.auth.controller;

import java.util.Map;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.api.auth.service.AuthService;
import com.example.onehada.db.dto.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/jwt")
    public ResponseEntity<?> generateJwt(@RequestBody Map<String, Object> payload) {
        try {

            String email = (String) payload.get("email");
            String provider = (String) payload.get("provider");
            AuthResponse tokens = authService.generateTokens(email, provider);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(401, "UNAUTHORIZED", "jwt: " + e.getMessage(), null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @Data
    @AllArgsConstructor
	static
	class ErrorResponse {
        private String message;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth test successful!");
    }
}
