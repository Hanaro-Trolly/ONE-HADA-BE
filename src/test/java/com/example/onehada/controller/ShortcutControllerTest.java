package com.example.onehada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.example.onehada.customer.shortcut.Shortcut;
import com.example.onehada.customer.shortcut.ShortcutDTO;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.shortcut.ShortcutService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShortcutControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthService authService;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@Autowired
	private ShortcutService shortcutService;

	private String token;
	private User testUser1;

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
			.simplePassword("12345678")
			.build();
		userRepository.save(testUser1);

		objectMapper = new ObjectMapper();

		Shortcut testShortcut1 = new Shortcut();
		testShortcut1.setUser(testUser1);
		testShortcut1.setShortcutName("ShortCut 1");
		testShortcut1.setShortcutElements("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		shortcutRepository.save(testShortcut1);

		Shortcut testShortcut2 = new Shortcut();
		testShortcut2.setUser(testUser1);
		testShortcut2.setShortcutName("ShortCut 2");
		testShortcut2.setShortcutElements("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		shortcutRepository.save(testShortcut2);

		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());

		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());
	}

	@Test
	@Order(1)
	public void testGetShortcuts() throws Exception {
		mockMvc.perform(get("/api/shortcut")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.shortcuts[0].shortcutName").value("ShortCut 2"))
			.andExpect(jsonPath("$.data.shortcuts[1].shortcutName").value("ShortCut 1"))
			.andDo(print());
	}

	@Test
	@Order(2)
	public void testCreateShortcut() throws Exception {
		ShortcutDTO test = ShortcutDTO.builder()
			.shortcutName("New Shortcut")
			.shortcutElements(Map.of("type", "transfer", "amount", "20000"))
			.build();
		String testJson = objectMapper.writeValueAsString(test);
		mockMvc.perform(post("/api/shortcut")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("바로가기 등록 성공"))
			.andExpect(jsonPath("$.data.shortcutId").isNotEmpty())
			.andDo(print());
	}

	@Test
	@Order(3)
	public void testDeleteShortcut() throws Exception {
		Shortcut test = shortcutRepository.findAll().get(0);
		Long shortcutId = test.getShortcutId();
		mockMvc.perform(delete("/api/shortcut/" + shortcutId)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("바로가기가 삭제되었습니다."))
			.andDo(print());
	}

	@Test
	@Order(4)
	public void testUpdateFavorite() throws Exception {
		Map<String, String> updateFavorite = new HashMap<>();
		updateFavorite.put("isFavorite", "true");
		String testJson = objectMapper.writeValueAsString(updateFavorite);
		Long shortcutId = shortcutRepository.findAll().get(0).getShortcutId();
		mockMvc.perform(patch("/api/shortcut/" + shortcutId + "/favorite")
				.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("즐겨찾기 설정이 변경되었습니다."))
			.andDo(print());
	}

	@Test
	@Order(5)
	public void testGetFavorites() throws Exception {
		mockMvc.perform(get("/api/shortcut/favorite")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.shortcuts[0].isFavorite").value(true))
			.andDo(print());
	}

	@AfterAll
	public void afterAll() {
		userRepository.deleteAll();
	}

}
