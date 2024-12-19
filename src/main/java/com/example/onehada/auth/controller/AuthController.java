package com.example.onehada.auth.controller;

import java.util.Map;
import java.util.Optional;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.dto.AuthResponseDTO;
import com.example.onehada.auth.dto.RefreshTokenRequestDTO;
import com.example.onehada.auth.dto.RegisterRequestDTO;
import com.example.onehada.auth.dto.SignInRequestDTO;
import com.example.onehada.auth.dto.SignInResponseDTO;
import com.example.onehada.auth.dto.SignInResponseDataDTO;
import com.example.onehada.auth.dto.VerifyPasswordRequestDTO;
import com.example.onehada.auth.service.AuthService;
import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.customer.user.User;
import com.example.onehada.exception.NotFoundException;
import com.example.onehada.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
@RequestMapping("/api/cert")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    //토큰 생성 테스트용
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

    // (비상용) 일반 로그인
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

    @Operation(summary = "소셜 로그인", description = "소셜 로그인을 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO request) {
        try {
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

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResult> register(@RequestBody RegisterRequestDTO request) {
        try {
            // Validate simple password
            if (request.getSimplePassword() == null || request.getSimplePassword().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResult(400, "BAD_REQUEST", "간편 비밀번호를 입력해주세요.", null));
            }

            ApiResult response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResult(400, "BAD_REQUEST", "회원가입에 실패했습니다: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        try {
            AuthResponseDTO newTokens = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(new ApiResult(200, "OK", "토큰 갱신 성공", newTokens));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResult(401, "UNAUTHORIZED", "토큰 갱신 실패: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "로그아웃", description = "현재 사용자의 토큰을 무효화합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);  // 전체 토큰 문자열을 전달
            return ResponseEntity.ok(new ApiResult(200, "OK", "로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResult(400, "BAD_REQUEST", "로그아웃 실패: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "비밀번호 확인", description = "사용자의 간편 비밀번호를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/verify")
    public ResponseEntity<ApiResult> verifyPassword(
        @RequestHeader("Authorization") String token,
        @RequestBody VerifyPasswordRequestDTO request) {
        try {
            // Bearer 토큰에서 실제 토큰 추출
            String accessToken = token.substring(7);

            // JWT에서 사용자 이메일 추출
            String email = jwtService.extractEmail(accessToken);

            // 비밀번호 검증
            authService.verifyPassword(email, request.getSimplePassword());

            return ResponseEntity.ok(new ApiResult(
                200,
                "OK",
                "인증 성공",
                null
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResult(
                    404,
                    "NOT_FOUND",
                    "사용자를 찾을 수 없습니다.",
                    null
                ));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResult(
                    401,
                    "UNAUTHORIZED",
                    "인증실패: 잘못된 비밀번호 입니다.",
                    null
                ));
        }
    }
}
