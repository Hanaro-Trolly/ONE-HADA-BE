package com.example.onehada.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.onehada.redis.RedisService;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.access.token.expiration}")
    private Long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh.token.expiration}")
    private Long refreshTokenExpiration;

    private final RedisService redisService;

    public JwtService(RedisService redisService) {
        this.redisService = redisService;
    }

    public String generateAccessToken(String userEmail, Long userId) {
        return buildToken(userEmail, userId, accessTokenExpiration);
    }

    public String generateRefreshToken(String userEmail, Long userId) {
        return buildToken(userEmail, userId, refreshTokenExpiration);
    }

    private String buildToken(String userEmail, Long userId, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", Collections.singletonList("ROLE_USER"));

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userEmail)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }


    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId",Long.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // public boolean isTokenValid(String token, String userEmail) {
    //     final String email = extractEmail(token);
    //     return (email.equals(userEmail)) && !isTokenExpired(token);
    // }


    public boolean isValidToken(String token) {
        try {
            // 블랙리스트 체크
            if (redisService.isBlacklisted(token)) {
                return false;
            }

            // JWT 유효성 검증
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""));

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Long getExpirationFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
