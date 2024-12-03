package com.example.onehada.api.auth.controller;

import java.util.Map;
import java.util.Optional;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.api.auth.dto.SignInRequest;
import com.example.onehada.api.auth.dto.SignInResponse;
import com.example.onehada.api.auth.service.AuthService;
import com.example.onehada.api.service.UserService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userRepository;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInRequest request) {
        try {
            // Check if user exists
            Optional<User> existingUser = switch (request.getProvider().toLowerCase()) {
                case "google" -> userRepository.findByUserEmailAndUserGoogleIdIsNotNull(request.getEmail());
                case "kakao" -> userRepository.findByUserEmailAndUserKakaoIdIsNotNull(request.getEmail());
                case "naver" -> userRepository.findByUserEmailAndUserNaverIdIsNotNull(request.getEmail());
                default -> throw new RuntimeException("Unsupported provider: " + request.getProvider());
            };

            if (existingUser.isPresent()) {
                // Existing user - generate tokens
                User user = existingUser.get();
                AuthResponse tokens = authService.generateTokens(
                    user.getUserEmail(),
                    user.getUserName(),
                    user.getUserId()
                );

                SignInResponse signInResponse = SignInResponse.builder()
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .userId(user.getUserId().toString())
                    .build();

                return ResponseEntity.ok(new ApiResponse(
                    200,
                    "EXIST",
                    "기존 로그인 성공",
                    signInResponse
                ));
            } else {
                // New user
                return ResponseEntity.ok(new ApiResponse(
                    200,
                    "NEW",
                    "새로운 사용자 입니다.",
                    null
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(
                400,
                "BAD_REQUEST",
                "소셜 로그인 실패. " + e.getMessage(),
                null
            ));
        }
    }

    @PostMapping("/jwt")
    public ResponseEntity<?> generateJwt(@RequestBody Map<String, Object> payload) {
        try {

            String email = (String) payload.get("email");
            String name = (String) payload.get("name");
            Long userId = (Long) payload.get("userId");

            AuthResponse tokens = authService.generateTokens(email, name, userId);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
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
