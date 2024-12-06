package com.example.onehada.api.auth.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.api.auth.dto.PasswordRequest;
import com.example.onehada.api.auth.dto.RegisterRequest;
import com.example.onehada.db.dto.ApiResponse;
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
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private Long refreshTokenExpiration;

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

        String accessToken = jwtService.generateAccessToken(user.getUserEmail(), user.getUserId());
        String refreshToken = jwtService.generateRefreshToken(user.getUserEmail(), user.getUserId());

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
    public AuthResponse generateTokens(String email, String name, Long userId) {
        // 지금은 유저등록 안되어있어서 주석처리 ->
        // 1. 이메일 + 프로바이더 확인하여 회원인지 아닌지 판별
        // 2. 없을 경우 회원가입
        // User user = userRepository.findByUserEmail(email)
        //     .orElseThrow(() -> new RuntimeException("User not found"));

        // Access Token과 Refresh Token 생성
        String accessToken = jwtService.generateAccessToken(email, userId);
        String refreshToken = jwtService.generateRefreshToken(email, userId);

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


    //Validation
    public void validateAccountOwnership(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다. ID: " + accountId));

        if (!account.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("User does not have access to this account");
        }
    }

    public Optional<User> findUserBySocialId(String provider, String email) {
        switch(provider.toLowerCase()) {
            case "google":
                return userRepository.findByUserGoogleId(email);
            case "kakao":
                return userRepository.findByUserKakaoId(email);
            case "naver":
                return userRepository.findByUserNaverId(email);
            default:
                throw new RuntimeException("지원하지 않는 소셜 로그인 제공자입니다.");
        }
    }

    public ApiResponse register(RegisterRequest request) {
        try {
            // 소셜 계정 중 하나라도 있는지 확인
            String primaryEmail = Stream.of(request.getGoogle(), request.getKakao(), request.getNaver())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("소셜 이메일이 필요합니다"));

            Optional<User> existingUser = userRepository.findByUserEmail(primaryEmail);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // 소셜 계정 정보 업데이트
                if (request.getGoogle() != null) user.setUserGoogleId(request.getGoogle());
                if (request.getKakao() != null) user.setUserKakaoId(request.getKakao());
                if (request.getNaver() != null) user.setUserNaverId(request.getNaver());

                userRepository.save(user);
                return new ApiResponse(200, "EXIST", "계정연동 성공", null);
            }

            String formattedBirth = request.getBirth().replaceAll("-", "");
            // 신규 사용자 생성
            User newUser = User.builder()
                .userName(request.getName())
                .userGender(request.getGender())
                .userEmail(request.getGoogle() != null ? request.getGoogle() :
                    request.getKakao() != null ? request.getKakao() : request.getNaver())
                .phoneNumber(request.getPhone())
                .userAddress(request.getAddress())
                .userBirth(formattedBirth)
                .userGoogleId(request.getGoogle())
                .userKakaoId(request.getKakao())
                .userNaverId(request.getNaver())
                .simplePassword("000000")
                .build();

            User savedUser = userRepository.save(newUser);
            System.out.println("Saved user: " + savedUser);  // 로그 추가
            return new ApiResponse(200, "NEW", "회원가입 성공", null);

        } catch (Exception e) {
            e.printStackTrace();  // 에러 로그 추가
            throw new RuntimeException("회원가입 실패: " + e.getMessage());
        }
    }
    public ApiResponse setPassword(PasswordRequest request) {
        User user = userRepository.findByUserEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setSimplePassword(request.getPassword());
        userRepository.save(user);

        return new ApiResponse(200, "OK", "간편비밀번호 등록 성공", null);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isValidToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtService.extractEmail(refreshToken);
        Long userId = jwtService.extractUserId(refreshToken);

        // Redis에 저장된 리프레시 토큰과 비교
        String storedRefreshToken = redisService.getRefreshToken(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("토큰이 일치하지 않습니다.");
        }

        User user = userRepository.findByUserEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 토큰 발급
        return generateTokens(email, user.getUserName(), userId);
    }

    public void logout(String token) {
        // Bearer 토큰에서 실제 토큰 값 추출
        String accessToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtService.extractEmail(accessToken);

        // Redis에서 토큰 조회
        String storedAccessToken = redisService.getAccessToken(email);
        if (storedAccessToken == null) {
            throw new RuntimeException("이미 로그아웃되었거나 유효하지 않은 세션입니다.");
        }

        // 토큰을 블랙리스트에 추가하고 Redis에서 제거
        Long expiration = jwtService.getExpirationFromToken(accessToken);
        redisService.addToBlacklist(accessToken, expiration);
        redisService.deleteValue("access:" + email);
        redisService.deleteValue("refresh:" + email);
    }

}
