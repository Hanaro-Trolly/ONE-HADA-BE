package com.example.onehada.redis;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

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


	@Test
	@Order(1)
	public void testSaveTransferDetails_Success() throws Exception {
		// 준비: 테스트 데이터를 설정
		Map<String, String> transferRequest = new HashMap<>();
		transferRequest.put("key1", "value1");
		transferRequest.put("key2", "value2");

		// 요청을 보내고 응답 검증
		mockMvc.perform(post("/api/redis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> 정보저장 -> 정상적으로 수행했습니다."))
			.andExpect(jsonPath("$.data.key1").value("value1"))
			.andExpect(jsonPath("$.data.key2").value("value2"));
	}
}
