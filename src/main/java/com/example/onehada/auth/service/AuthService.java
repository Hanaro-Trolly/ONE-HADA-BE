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
import com.example.onehada.exception.UnauthorizedException;
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

    // 소셜 로그인으로 사용자 찾기
    public Optional<User> findUserBySocialId(String provider, String email) {
        // 먼저 해당 소셜 ID로 사용자 찾기 시도
        Optional<User> user = switch(provider.toLowerCase()) {
            case "google" -> userRepository.findByUserGoogleId(email);
            case "kakao" -> userRepository.findByUserKakaoId(email);
            case "naver" -> userRepository.findByUserNaverId(email);
            default -> throw new RuntimeException("지원하지 않는 소셜 로그인입니다.");
        };

        return user;
    }

    public ApiResponse register(RegisterRequestDTO request) {
        try {
            // 소셜 이메일 확인
            String primaryEmail = Stream.of(request.getGoogle(), request.getKakao(), request.getNaver())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("소셜 이메일이 필요합니다"));

            // 생년월일과 전화번호 형식 통일
            String formattedBirth = request.getBirth().replaceAll("-", "");
            String formattedPhone = request.getPhone().replaceAll("-", "");

            // 기존 사용자 찾기 (이름, 성별, 생년월일, 전화번호로 매칭)
            Optional<User> existingUser = userRepository.findAll().stream()
                .filter(user ->
                    user.getUserName().equals(request.getName()) &&
                        user.getUserGender().equals(request.getGender()) &&
                        user.getUserBirth().equals(formattedBirth) &&
                        user.getPhoneNumber().replaceAll("-", "").equals(formattedPhone)
                )
                .findFirst();

            // 기존 사용자가 있는 경우
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // 소셜 ID 업데이트
                if (request.getGoogle() != null) user.setUserGoogleId(request.getGoogle());
                if (request.getKakao() != null) user.setUserKakaoId(request.getKakao());
                if (request.getNaver() != null) user.setUserNaverId(request.getNaver());

                userRepository.save(user);
                return new ApiResponse(200, "EXIST", "계정연동 성공", null);
            }

            // 새로운 사용자 생성
            User newUser = User.builder()
                .userName(request.getName())
                .userGender(request.getGender())
                .userEmail(primaryEmail)
                .phoneNumber(formattedPhone)
                .userAddress(request.getAddress())
                .userBirth(formattedBirth)
                .userGoogleId(request.getGoogle())
                .userKakaoId(request.getKakao())
                .userNaverId(request.getNaver())
                .simplePassword("000000")  // 초기 비밀번호
                .build();

            userRepository.save(newUser);
            return new ApiResponse(200, "NEW", "회원가입 성공", null);

        } catch (Exception e) {
            e.printStackTrace();
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

    public void verifyPassword(String email, String simplePassword) {
        User user = userRepository.findByUserEmail(email)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        if (!user.getSimplePassword().equals(simplePassword)) {
            throw new UnauthorizedException("잘못된 비밀번호 입니다.");
        }
    }
}
