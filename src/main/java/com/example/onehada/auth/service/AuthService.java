package com.example.onehada.auth.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.dto.AuthResponseDTO;
import com.example.onehada.auth.dto.PasswordRequestDTO;
import com.example.onehada.auth.dto.RegisterRequestDTO;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.customer.account.Account;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.redis.RedisService;
import com.example.onehada.exception.ForbiddenException;
import com.example.onehada.exception.NotFoundException;

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

    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByUserEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getSimplePassword().equals(request.getSimplePassword())) {
            throw new RuntimeException("Invalid password");
        }

        return generateTokens(user.getUserEmail(), user.getUserName(), user.getUserId());
    }


    public AuthResponseDTO generateTokens(String email, String name, Long userId) {
        String accessToken = jwtService.generateAccessToken(email, userId);
        String refreshToken = jwtService.generateRefreshToken(email, userId);

        // Redis에는 Refresh Token만 저장
        redisService.saveRefreshToken(email, refreshToken, refreshTokenExpiration);

        return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .email(email)
            .userName(name)
            .build();
    }


    //Validation
    public void validateAccountOwnership(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("해당 계좌를 찾을 수 없습니다. ID: " + accountId));

        if (!account.getUser().getUserId().equals(userId)) {
			throw new ForbiddenException("User does not have access to this account");
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

    public ApiResponse register(RegisterRequestDTO request) {
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
    public ApiResponse setPassword(PasswordRequestDTO request) {
        User user = userRepository.findByUserEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setSimplePassword(request.getPassword());
        userRepository.save(user);

        return new ApiResponse(200, "OK", "간편비밀번호 등록 성공", null);
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
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

        return generateTokens(email, user.getUserName(), userId);
    }

    public void logout(String token) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtService.extractEmail(accessToken);

        // Access Token을 블랙리스트에 추가
        Long expiration = jwtService.getExpirationFromToken(accessToken);
        redisService.addToBlacklist(accessToken, expiration);

        // Refresh Token 삭제
        redisService.deleteRefreshToken(email);
    }

}
