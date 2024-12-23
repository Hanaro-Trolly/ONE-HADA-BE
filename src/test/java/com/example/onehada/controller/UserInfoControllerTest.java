package com.example.onehada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.service.AuthService;
import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserInfoControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthService authService;
	@Autowired
	private UserRepository userRepository;

	private User testUser1;
	private String token;

	@BeforeAll
	public void setUp() {
		userRepository.deleteAll();

		testUser1 = User.builder()
			.userName("testUser1")
			.userEmail("testuser1@example.com")
			.userGender("M")
			.phoneNumber("01012345678")
			.userAddress("서울시 강남구")
			.userBirth("19900101")
			.simplePassword("123456")
			.build();
		userRepository.save(testUser1);

		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());

		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());
	}

	@Test
	@Order(1)
	public void testGetUser() throws Exception {
		mockMvc.perform(get("/api/user")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.userName").value(testUser1.getUserName()))
			.andDo(print());
	}

	@Test
	@Order(2)
	public void testUpdateUserSuccess() throws Exception {
		Map<String, String> updateUserInfo = new HashMap<>();
		updateUserInfo.put("userPhone", "987-6543-2121");
		updateUserInfo.put("userAddress", "NewYork");
		String updateJson = objectMapper.writeValueAsString(updateUserInfo);

		mockMvc.perform(patch("/api/user")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(updateJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.userPhone").value(updateUserInfo.get("userPhone")))
			.andExpect(jsonPath("$.data.userAddress").value(updateUserInfo.get("userAddress")))
			.andDo(print());
	}

	@Test
	@Order(3)
	public void testUpdateUserFail() throws Exception {
		Map<String, String> updateUserInfo = new HashMap<>();
		updateUserInfo.put("userPhone", "");
		updateUserInfo.put("userAddress", "");
		String updateJson = objectMapper.writeValueAsString(updateUserInfo);

		mockMvc.perform(patch("/api/user")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(updateJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.message").value("잘못된 형식의 데이터입니다."))
			.andDo(print());
	}

	@Test
	@Order(4)
	public void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/user")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("사용자 탈퇴 완료"))
			.andDo(print());
	}

	@Test
	@AfterAll
	public void afterAll() {
		userRepository.deleteAll();
	}
}
