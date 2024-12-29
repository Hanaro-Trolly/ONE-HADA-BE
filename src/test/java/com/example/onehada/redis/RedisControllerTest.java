package com.example.onehada.redis;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.exception.BaseException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	private String testToken;

	@BeforeAll
	public void setUp() {
		// 테스트 사용자 생성
		User testUser = User.builder()
			.userId(1L)
			.userEmail("test@example.com")
			.userName("테스트유저")
			.userGender("M")
			.userBirth("20000101")
			.phoneNumber("01012345678")
			.simplePassword("1234")
			.userAddress("서울시 강남구")
			.build();

		// 테스트 사용자를 데이터베이스에 저장
		userRepository.save(testUser);

		// 테스트 토큰 생성
		testToken = "Bearer " + jwtService.generateAccessToken("test@example.com", 1L);
	}

	@AfterAll
	public void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	@Order(1)
	public void testSaveTransferDetails_Success() throws Exception {
		Map<String, String> transferRequest = new HashMap<>();
		transferRequest.put("key1", "value11");
		transferRequest.put("key2", "value2");

		mockMvc.perform(post("/api/redis")
				.header("Authorization", testToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> 정보저장 -> 정상적으로 수행했습니다."))
			.andExpect(jsonPath("$.data.key1").value("value11"))
			.andExpect(jsonPath("$.data.key2").value("value2"));
	}

	@Test
	@Order(2)
	public void testGetValidationValue_Success() throws Exception {
		List<String> keys = Arrays.asList("key1", "key2");

		mockMvc.perform(post("/api/redis/get")
				.header("Authorization", testToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(keys)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> 단일 정보 조회 -> 응답을 정상적으로 수행했습니다."))
			.andExpect(jsonPath("$.data.key1").value("value11"))
			.andExpect(jsonPath("$.data.key2").value("value2"));
	}

	@Test
	@Order(3)
	public void testUpdateTransferDetails_Success() throws Exception {
		Map<String, String> transferRequest = new HashMap<>();
		transferRequest.put("key1", "updatedValue1");
		transferRequest.put("key2", "updatedValue2");

		mockMvc.perform(patch("/api/redis")
				.header("Authorization", testToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> -> 수정이 정상적으로 수행되었습니다."))
			.andExpect(jsonPath("$.data.key1").value("updatedValue1"))
			.andExpect(jsonPath("$.data.key2").value("updatedValue2"));
	}

	@Test
	@Order(4)
	public void testDeleteTransferDetails_Success() throws Exception {
		List<String> keys = Arrays.asList("key1", "key2");

		mockMvc.perform(post("/api/redis/delete")
				.header("Authorization", testToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(keys)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> -> 삭제가 정상적으로 수행되었습니다."))
			.andExpect(jsonPath("$.data").isArray());
	}
}
