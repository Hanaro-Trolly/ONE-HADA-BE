package com.example.onehada.controller;

import static org.junit.jupiter.api.Assertions.*;
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
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.agent.AgentRepository;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.shortcut.Shortcut;
import com.example.onehada.customer.shortcut.ShortcutDTO;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.shortcut.ShortcutService;
import com.example.onehada.customer.transaction.TransactionRepository;
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
	private HistoryRepository historyRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AgentRepository agentRepository;

	@Autowired
	private TransactionRepository transactionRepository;


	private String token;
	private String token2;
	private Shortcut testShortcut1;

	@Autowired
	private ShortcutService shortcutService;

	@BeforeAll
	public void setUp() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
		shortcutRepository.deleteAll();
		historyRepository.deleteAll();
		consultationRepository.deleteAll();
		agentRepository.deleteAll();
		userRepository.deleteAll();

		User testUser1 = User.builder()
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

		testShortcut1 = Shortcut.builder()
			.user(testUser1)
			.shortcutName("ShortCut 1")
			.shortcutElements("{\"key1\":\"value1\",\"key2\":\"value2\"}")
			.build();
		shortcutRepository.save(testShortcut1);

		Shortcut testShortcut2 = Shortcut.builder()
			.user(testUser1)
			.shortcutName("ShortCut 2")
			.shortcutElements("{\"key1\":\"value1\",\"key2\":\"value2\"}")
			.build();
		shortcutRepository.save(testShortcut2);


		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());

		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());

		User testUser2 = User.builder()
			.userName("testUser2")
			.userEmail("testuser2@example.com")
			.userGender("M")
			.phoneNumber("01012345678")
			.userAddress("서울시 강남구")
			.userBirth("19900101")
			.simplePassword("12345678")
			.build();
		userRepository.save(testUser2);

		authService.login(AuthRequestDTO.builder()
			.email(testUser2.getUserEmail())
			.simplePassword(testUser2.getSimplePassword())
			.build());

		this.token2 = jwtService.generateAccessToken(testUser2.getUserEmail(), testUser2.getUserId());

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
	public void testGetShortcutElementsInvalidJson() {
		Shortcut shortcut = new Shortcut();
		shortcut.setShortcutElements("{\"key1\":\"value1\",\"key2\":\"value2\"");

		Exception exception = assertThrows(RuntimeException.class, () -> ShortcutService.getShortcutElements(shortcut));

		assertTrue(exception.getMessage().contains("올바른 JSON 형식이 아닙니다."));
	}

	@Test
	@Order(3)
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
	@Order(4)
	public void testCreateShortcutObjectMapper() {
		Object testObject = new Object();

		ShortcutDTO shortcutDTO = ShortcutDTO.builder()
			.shortcutName("Test Shortcut with ObjectMapper")
			.shortcutElements(Map.of("type", testObject))
			.build();

		Exception exception = assertThrows(RuntimeException.class, () -> shortcutService.createShortcut(shortcutDTO,
			"Bearer " + token));

		assertNotNull(exception.getMessage());
	}

	@Test
	@Order(5)
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
	@Order(6)
	public void testGetFavorites() throws Exception {
		mockMvc.perform(get("/api/shortcut/favorite")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.shortcuts[0].isFavorite").value(true))
			.andDo(print());
	}

	@Test
	@Order(7)
	public void testUpdateFavoriteNotFoundShortcut() throws Exception {
		long shortcutId = 2000000L;
		Map<String, String> updateFavorite = new HashMap<>();
		updateFavorite.put("isFavorite", "true");
		String testJson = objectMapper.writeValueAsString(updateFavorite);
		mockMvc.perform(patch("/api/shortcut/" + shortcutId + "/favorite")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(testJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(404))
			.andExpect(jsonPath("$.message").value("존재하지 않는 바로가기 입니다."))
			.andDo(print());
	}

	@Test
	@Order(8)
	public void testUpdateFavoriteNotFoundUser() throws Exception {
		long shortcutId = shortcutRepository.findAll().get(0).getShortcutId();
		Map<String, String> updateFavorite = new HashMap<>();
		updateFavorite.put("isFavorite", "true");
		String testJson = objectMapper.writeValueAsString(updateFavorite);
		mockMvc.perform(patch("/api/shortcut/" + shortcutId + "/favorite")
				.header("Authorization", "Bearer " + token2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(404))
			.andExpect(jsonPath("$.message").value("존재하지 않는 바로가기 입니다."))
			.andDo(print());
	}

	@Test
	@Order(9)
	public void testDeleteShortcut() throws Exception {
		Long shortcutId = testShortcut1.getShortcutId();
		mockMvc.perform(delete("/api/shortcut/" + shortcutId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("바로가기가 삭제되었습니다."))
			.andDo(print());
	}

	@Test
	@Order(10)
	public void testDeleteShortcutNotFound() throws Exception {
		long shortcutId = 999999L;
		mockMvc.perform(delete("/api/shortcut/" + shortcutId)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(404))
			.andExpect(jsonPath("$.message").value("존재하지 않는 바로가기입니다."))
			.andDo(print());
	}

	@AfterAll
	public void afterAll() {
		userRepository.deleteAll();
	}

}
