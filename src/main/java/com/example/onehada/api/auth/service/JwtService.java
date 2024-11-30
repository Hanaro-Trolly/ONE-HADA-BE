package com.example.onehada.api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String userEmail) {
        return buildToken(userEmail, accessTokenExpiration);
    }

    public String generateRefreshToken(String userEmail) {
        return buildToken(userEmail, refreshTokenExpiration);
    }

    private String buildToken(String userEmail, long expiration) {
        return Jwts.builder()
            .setSubject(userEmail)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // public List<String> extractRoles(String token) {
    //     Claims claims = extractAllClaims(token);
    //     Object rolesObject = claims.get("roles");
    //     List<String> roles = new ArrayList<>();
    //     if (rolesObject instanceof List) {
    //         for (Object role : (List<?>) rolesObject) {
    //             roles.add(role.toString());
    //         }
    //     }
    //     return roles;
    // }

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

    public boolean isTokenValid(String token, String userEmail) {
        final String email = extractEmail(token);
        return (email.equals(userEmail)) && !isTokenExpired(token);
    }


    public boolean isValidToken(String token) {
        try {
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

    public long getExpirationFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
