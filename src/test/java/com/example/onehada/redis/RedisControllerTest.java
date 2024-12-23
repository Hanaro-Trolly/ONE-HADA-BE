package com.example.onehada.redis;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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


	@Test
	@Order(1)
	public void testSaveTransferDetails_Success() throws Exception {
		// 준비: 테스트 데이터를 설정
		Map<String, String> transferRequest = new HashMap<>();
		transferRequest.put("key1", "value11");
		transferRequest.put("key2", "value2");

		// 요청을 보내고 응답 검증
		mockMvc.perform(post("/api/redis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> 정보저장 -> 정상적으로 수행했습니다."))
			.andExpect(jsonPath("$.data.key1").value("value11"))
			.andExpect(jsonPath("$.data.key2").value("value2"));
	}

	// @Test
	// @Order(2)
	// public void testSaveTransferDetails_Failure() throws Exception {
	// 	// 준비: 테스트 데이터를 설정
	// 	Map<String, String> transferRequest = new HashMap<>();
	// 	transferRequest.put("key1", "value1");
	// 	transferRequest.put("key2", "value2");
	//
	// 	doThrow(new BaseException("Redis 오류")).when(redisService).saveValue(anyString(), anyString());
	//
	// 	// 요청을 보내고 응답 검증
	// 	mockMvc.perform(post("/api/redis")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(transferRequest)))
	// 		.andExpect(status().isInternalServerError())
	// 		.andExpect(jsonPath("$.statusCode").value(500))
	// 		.andExpect(jsonPath("$.status").value("error"))
	// 		.andExpect(jsonPath("$.message").value("계좌 이체 정보를 저장하는 중 오류가 발생했습니다: Redis 오류"))
	// 		.andExpect(jsonPath("$.data").isEmpty());
	// }

	@Test
	@Order(2)
	public void testGetValidationValue_Success() throws Exception {
		List<String> keys = Arrays.asList("key1", "key2");
		Map<String, String> redisMockData = new HashMap<>();

		String requestBody = objectMapper.writeValueAsString(keys);

		mockMvc.perform(post("/api/redis/get")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
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
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(keys)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("<Redis> -> 삭제가 정상적으로 수행되었습니다."))
			.andExpect(jsonPath("$.data").isArray());
	}
}
