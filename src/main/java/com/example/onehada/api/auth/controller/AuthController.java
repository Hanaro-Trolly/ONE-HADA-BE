package com.example.onehada.api.auth.controller;

import java.util.Map;
import java.util.Optional;

import com.example.onehada.api.auth.dto.AuthRequestDTO;
import com.example.onehada.api.auth.dto.AuthResponseDTO;
import com.example.onehada.api.auth.dto.RefreshTokenRequestDTO;
import com.example.onehada.api.auth.dto.RegisterRequestDTO;
import com.example.onehada.api.auth.dto.SignInRequestDTO;
import com.example.onehada.api.auth.dto.SignInResponseDTO;
import com.example.onehada.api.auth.dto.SignInResponseDataDTO;
import com.example.onehada.api.auth.service.AuthService;
import com.example.onehada.api.auth.dto.PasswordRequestDTO;
import com.example.onehada.api.service.UserService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cert")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userRepository;

    @PostMapping("/jwt")
    public ResponseEntity<?> generateJwt(@RequestBody Map<String, Object> payload) {
        try {

            String email = (String) payload.get("email");
            String name = (String) payload.get("name");
            Long userId = (Long) payload.get("userId");

            AuthResponseDTO tokens = authService.generateTokens(email, name, userId);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
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

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth test successful!");
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO request) {
        try {
            // provider와 이메일로 기존 사용자 확인
            // AuthService를 통해 사용자 조회
            Optional<User> existingUser = authService.findUserBySocialId(request.getProvider(), request.getEmail());
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // 토큰 생성
                AuthResponseDTO tokens = authService.generateTokens(user.getUserEmail(), user.getUserName(), user.getUserId());

                return ResponseEntity.ok(SignInResponseDTO.builder()
                    .code(200)
                    .status("EXIST")
                    .message("기존 로그인 성공")
                    .data(SignInResponseDataDTO.builder()
                        .accessToken(tokens.getAccessToken())
                        .refreshToken(tokens.getRefreshToken())
                        .userId(String.valueOf(user.getUserId()))
                        .build())
                    .build());
            }
            return ResponseEntity.ok(SignInResponseDTO.builder()
                .code(200)
                .status("NEW")
                .message("새로운 사용자 입니다.")
                .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(SignInResponseDTO.builder()
                .code(400)
                .status("BAD_REQUEST")
                .message("소셜 로그인 실패. " + e.getMessage())
                .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequestDTO request) {
        try {
            ApiResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse(400, "BAD_REQUEST", "필수 정보를 입력해주세요.", null));
        }
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse> setPassword(@RequestBody PasswordRequestDTO request) {
        try {
            ApiResponse response = authService.setPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse(400, "BAD_REQUEST", "비밀번호를 등록할 수 없습니다.", null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        try {
            AuthResponseDTO newTokens = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(new ApiResponse(200, "OK", "토큰 갱신 성공", newTokens));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(401, "UNAUTHORIZED", "토큰 갱신 실패: " + e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);  // 전체 토큰 문자열을 전달
            return ResponseEntity.ok(new ApiResponse(200, "OK", "로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(400, "BAD_REQUEST", "로그아웃 실패: " + e.getMessage(), null));
        }
    }
}
