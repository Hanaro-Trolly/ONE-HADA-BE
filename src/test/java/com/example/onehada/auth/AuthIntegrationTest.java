package com.example.onehada.auth;

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.dto.AuthResponse;
import com.example.onehada.api.service.RedisService;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		// 테스트용 사용자 생성
		User testUser = User.builder()
			.userEmail("test@test.com")
			.userName("테스트")
			.simplePassword("1234")
			// 필요한 경우 다른 필수 필드들도 설정
			.userGender("M")
			.phoneNumber("01012345678")
			.userBirth("19900101")
			.build();
		
		userRepository.save(testUser);
	}

	@Test
	public void testLoginAndTokenStorage() throws Exception {
		// Given
		AuthRequest request = AuthRequest.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		// When
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		// Then
		AuthResponse response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			AuthResponse.class
		);

		assertNotNull(response.getAccessToken());
		assertNotNull(response.getRefreshToken());
		assertEquals("test@test.com", response.getEmail());

		// Redis 저장 확인
		String storedAccessToken = redisService.getAccessToken("test@test.com");
		String storedRefreshToken = redisService.getRefreshToken("test@test.com");

		assertNotNull(storedAccessToken);
		assertNotNull(storedRefreshToken);
		assertEquals(response.getAccessToken(), storedAccessToken);
	}

	@Test
	public void testLogoutAndBlacklist() throws Exception {
		// Given - 먼저 로그인
		AuthRequest request = AuthRequest.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponse response = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponse.class
		);

		// When - 로그아웃
		mockMvc.perform(post("/api/auth/logout")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isOk());

		// Then - 토큰이 블랙리스트에 있는지 확인
		assertTrue(redisService.isBlacklisted(response.getAccessToken()));

		// 로그아웃된 토큰으로 접근 시도
		mockMvc.perform(get("/api/auth/test")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void testProtectedEndpointWithValidToken() throws Exception {
		// Given - 로그인
		AuthRequest request = AuthRequest.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		AuthResponse response = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(),
			AuthResponse.class
		);

		// When & Then - 보호된 엔드포인트 접근
		mockMvc.perform(get("/api/auth/test")
				.header("Authorization", "Bearer " + response.getAccessToken()))
			.andExpect(status().isOk());
	}

	@Test
	public void testLoginWithInvalidCredentials() throws Exception {
		AuthRequest request = AuthRequest.builder()
			.email("wrong@email.com")
			.simplePassword("wrongpass")
			.build();

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized());
	}
}
