package com.example.onehada.auth;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.dto.AuthResponseDTO;
import com.example.onehada.auth.dto.RefreshTokenRequestDTO;
import com.example.onehada.auth.dto.RegisterRequestDTO;
import com.example.onehada.auth.dto.SignInRequestDTO;
import com.example.onehada.auth.dto.SignInResponseDTO;
import com.example.onehada.auth.dto.VerifyPasswordRequestDTO;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.transaction.TransactionRepository;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.redis.RedisService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트 환경의 설정 파일을 로드
@Transactional
public class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		transactionRepository.deleteAll();
		consultationRepository.deleteAll();
		shortcutRepository.deleteAll();
		historyRepository.deleteAll();
		accountRepository.deleteAll();
		userRepository.deleteAll();

		User testUser = User.builder()
			.userEmail("test@test.com")
			.userName("테스트")
			.simplePassword("1234")
			.userGender("M")
			.phoneNumber("01012345678")
			.userBirth("19900101")
			.build();

		userRepository.save(testUser);
	}

	@Test
	public void setUptest() {
		// Given 유저 생성

		// When 저장

		// Then
		Optional<User> retrievedUser = userRepository.findByUserEmail("test@test.com");
		assertTrue(retrievedUser.isPresent(), "User should be saved in the database");

		User savedUser = retrievedUser.get();
		assertEquals("test@test.com", savedUser.getUserEmail());
		assertEquals("테스트", savedUser.getUserName());
		assertEquals("1234", savedUser.getSimplePassword());
		assertEquals("M", savedUser.getUserGender());
		assertEquals("01012345678", savedUser.getPhoneNumber());
		assertEquals("19900101", savedUser.getUserBirth());
	}

	@Test
	public void LoginAndTokenStorage() throws Exception {
		AuthRequestDTO request = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		// When
		MvcResult result = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		// Then
		AuthResponseDTO response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		assertNotNull(response.getAccessToken());
		assertNotNull(response.getRefreshToken());
		assertEquals("test@test.com", response.getEmail());

		// Redis에는 Refresh Token만 저장되어 있어야 함
		String storedRefreshToken = redisService.getRefreshToken("test@test.com");
		assertNotNull(storedRefreshToken);
		assertEquals(response.getRefreshToken(), storedRefreshToken);
	}

	@Test
	public void LogoutAndBlacklist() throws Exception {
		// Given - 로그인
		AuthRequestDTO request = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponseDTO response = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		// When - 로그아웃
		mockMvc.perform(post("/api/cert/logout")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isOk());

		// Then
		// 1. Access Token이 블랙리스트에 있는지 확인
		assertTrue(redisService.isBlacklisted(response.getAccessToken()));

		// 2. Refresh Token이 삭제되었는지 확인
		assertNull(redisService.getRefreshToken("test@test.com"));

		// 3. 로그아웃된 토큰으로 접근 시도
		mockMvc.perform(get("/api/cert/test")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void ProtectedEndpointWithValidToken() throws Exception {
		// Given - 로그인
		AuthRequestDTO request = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponseDTO response = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		// When & Then - 보호된 엔드포인트 접근
		mockMvc.perform(get("/api/cert/test")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isOk());
	}

	@Test
	public void LoginWithInvalidCredentials() throws Exception {
		AuthRequestDTO request = AuthRequestDTO.builder()
			.email("wrong@email.com")
			.simplePassword("wrongpass")
			.build();

		mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void RefreshTokenSuccess() throws Exception {
		// Given - 먼저 로그인
		AuthRequestDTO request = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponseDTO response = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		// When - Refresh Token으로 새 토큰 요청
		RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
			.refreshToken(response.getRefreshToken())
			.build();

		MvcResult refreshResult = mockMvc.perform(post("/api/cert/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshRequest)))
			.andExpect(status().isOk())
			.andReturn();

		// Then
		ApiResult apiResult = objectMapper.readValue(
			refreshResult.getResponse().getContentAsString(),
			ApiResult.class
		);

		assertEquals(200, apiResult.getCode());
		assertNotNull(((Map<String, String>)apiResult.getData()).get("accessToken"));
		assertNotNull(((Map<String, String>)apiResult.getData()).get("refreshToken"));
	}

	@Test
	public void RefreshTokenWithInvalidToken() throws Exception {
		// Given - 잘못된 Refresh Token
		RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
			.refreshToken("invalid.refresh.token")
			.build();

		// When & Then
		mockMvc.perform(post("/api/cert/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshRequest)))
			.andExpect(status().isUnauthorized());
	}

	// 회원가입 관련 테스트
	@Test
	public void RegisterNewUser() throws Exception {
		// Given
		RegisterRequestDTO request = RegisterRequestDTO.builder()
			.name("신규유저")
			.gender("F")
			.birth("1995-01-01")
			.phone("010-9999-8888")
			.address("서울시 강남구")
			.google("newuser@gmail.com")
			.simplePassword("123456")
			.build();

		// When
		MvcResult result = mockMvc.perform(post("/api/cert/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		// Then
		ApiResult response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			ApiResult.class
		);

		assertEquals(200, response.getCode());
		assertEquals("NEW", response.getStatus());

		// DB 확인
		Optional<User> savedUser = userRepository.findByUserGoogleId("newuser@gmail.com");
		assertTrue(savedUser.isPresent());
		assertEquals("신규유저", savedUser.get().getUserName());
	}

	@Test
	public void RegisterWithInvalidPassword() throws Exception {
		// Given - 5자리 비밀번호 (최소 6자리 필요)
		RegisterRequestDTO request = RegisterRequestDTO.builder()
			.name("신규유저")
			.gender("F")
			.birth("1995-01-01")
			.phone("010-9999-8888")
			.google("newuser@gmail.com")
			.simplePassword("12345")  // 5자리 비밀번호
			.build();

		// When & Then
		mockMvc.perform(post("/api/cert/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	// 소셜 로그인 관련 테스트
	@Test
	public void SignInWithGoogleNewUser() throws Exception {
		// Given
		SignInRequestDTO request = SignInRequestDTO.builder()
			.provider("google")
			.email("newgoogle@gmail.com")
			.build();

		// When
		MvcResult result = mockMvc.perform(post("/api/cert/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		// Then
		SignInResponseDTO response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			SignInResponseDTO.class
		);

		assertEquals(200, response.getCode());
		assertEquals("NEW", response.getStatus());
	}

	@Test
	public void SignInWithInvalidProvider() throws Exception {
		// Given
		SignInRequestDTO request = SignInRequestDTO.builder()
			.provider("invalid")
			.email("test@test.com")
			.build();

		// When & Then
		MvcResult result = mockMvc.perform(post("/api/cert/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andReturn();

		SignInResponseDTO response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			SignInResponseDTO.class
		);

		assertEquals(400, response.getCode());
		assertEquals("BAD_REQUEST", response.getStatus());
	}

	// 비밀번호 검증 관련 테스트
	@Test
	public void VerifyCorrectPassword() throws Exception {
		// Given - 먼저 로그인하여 토큰 얻기
		AuthRequestDTO loginRequest = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponseDTO loginResponse = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		// When - 비밀번호 검증 요청
		VerifyPasswordRequestDTO verifyRequest = VerifyPasswordRequestDTO.builder()
			.simplePassword("1234")
			.build();

		// Then
		mockMvc.perform(post("/api/cert/verify")
				.header("Authorization", "Bearer " + loginResponse.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifyRequest)))
			.andExpect(status().isOk());
	}

	@Test
	public void VerifyIncorrectPassword() throws Exception {
		// Given - 먼저 로그인하여 토큰 얻기
		AuthRequestDTO loginRequest = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponseDTO loginResponse = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		// When - 잘못된 비밀번호로 검증 요청
		VerifyPasswordRequestDTO verifyRequest = VerifyPasswordRequestDTO.builder()
			.simplePassword("wrong")
			.build();

		// Then
		mockMvc.perform(post("/api/cert/verify")
				.header("Authorization", "Bearer " + loginResponse.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifyRequest)))
			.andExpect(status().isUnauthorized());
	}
}
