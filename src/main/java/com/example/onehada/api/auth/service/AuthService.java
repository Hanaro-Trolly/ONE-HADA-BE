package com.example.onehada.api.auth.service;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.AccountRepository;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.api.service.RedisService;
import com.example.onehada.exception.account.AccountNotFoundException;
import com.example.onehada.exception.authorization.AccessDeniedException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    // public AuthService(AccountRepository accountRepository) {
    //     this.accountRepository = accountRepository;
    // }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUserEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new RuntimeException("Invalid password");
        }

        System.out.println("AuthService.login"+user);

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


    //Validation
    public void validateAccountOwnership(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다. ID: " + accountId));

        if (account.getUser().getUserId() != userId) {
			throw new AccessDeniedException("User does not have access to this account");
		}
    }


}
