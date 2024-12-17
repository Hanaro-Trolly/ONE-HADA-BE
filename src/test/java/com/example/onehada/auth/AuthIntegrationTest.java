package com.example.onehada.auth;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.dto.AuthResponseDTO;
import com.example.onehada.customer.shortcut.ShortcutRepository;
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
	private UserRepository userRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@BeforeEach
	void setUp() {
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
}
