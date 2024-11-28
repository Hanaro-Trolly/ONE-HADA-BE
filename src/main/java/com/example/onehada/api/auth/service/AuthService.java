package com.example.onehada.api.auth.service;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.api.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUserEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user.getUserEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getUserEmail());

        // Redis에 토큰 저장 (만료시간 설정)
        redisService.saveAccessToken(user.getUserEmail(), accessToken, accessTokenExpiration);
        redisService.saveRefreshToken(user.getUserEmail(), refreshToken, refreshTokenExpiration);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .email(user.getUserEmail())
            .userName(user.getUserName())
            .build();
    }

    // Access Token과 Refresh Token 발급 및 Redis 저장
    public AuthResponse generateTokens(String email, String name) {
        // 지금은 유저등록 안되어있어서 주석처리
        // User user = userRepository.findByUserEmail(email)
        //     .orElseThrow(() -> new RuntimeException("User not found"));

        // Access Token과 Refresh Token 생성
        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        // Redis에 Refresh Token 저장
        redisService.saveAccessToken(email, accessToken, accessTokenExpiration);
        redisService.saveRefreshToken(email, refreshToken, refreshTokenExpiration);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .email(email)
            .userName(name)
            .build();
    }

    public void logout(String token) {
        String email = jwtService.extractEmail(token.substring(7));
        String accessToken = redisService.getAccessToken(email);

        if (accessToken != null) {
            // 현재 토큰을 블랙리스트에 추가
            redisService.addToBlacklist(accessToken,
                jwtService.getExpirationFromToken(accessToken));

            // Redis에서 토큰들 삭제
            redisService.deleteValue("access:" + email);
            redisService.deleteValue("refresh:" + email);
        }
    }
}
