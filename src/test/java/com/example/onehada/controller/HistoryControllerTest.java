package com.example.onehada.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.service.AuthService;
import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.History;
import com.example.onehada.customer.history.HistoryDTO;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.history.HistoryService;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.transaction.TransactionRepository;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HistoryControllerTest {

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
	private HistoryService historyService;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private String token;
	private String token2;
	private User testUser1;

	@BeforeAll
	public void setUp() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
		consultationRepository.deleteAll();
		shortcutRepository.deleteAll();
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

		objectMapper = new ObjectMapper();
		History testHistory1 = History.builder()
			.user(testUser1)
			.historyName("History 1")
			.historyElements("{\"key1\":\"value1\",\"key2\":\"value2\"}")
			.build();
		historyRepository.save(testHistory1);
		History testHistory2 = History.builder()
			.user(testUser1)
			.historyName("History 2")
			.historyElements("{\"key1\":\"value1\",\"key2\":\"value2\"}")
			.build();
		historyRepository.save(testHistory2);

		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());

		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());

		authService.login(AuthRequestDTO.builder()
			.email(testUser2.getUserEmail())
			.simplePassword(testUser2.getSimplePassword())
			.build());

		this.token2 = jwtService.generateAccessToken(testUser2.getUserEmail(), testUser2.getUserId());

	}

	@Test
	@Order(1)
	public void testGetHistories() throws Exception {
		mockMvc.perform(get("/api/history")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status").value("OK"))
		.andExpect(jsonPath("$.data.histories[0].historyName").value("History 2"))
		.andExpect(jsonPath("$.data.histories[1].historyName").value("History 1"))
			.andDo(print());
	}

	@Test
	@Order(2)
	public void testGetHistory() throws Exception {
		History history = historyRepository.findByUser(testUser1).get(0);
		Long historyId = history.getHistoryId();
		mockMvc.perform(get("/api/history/" + historyId).header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.data.historyName").value(history.getHistoryName()))
			.andExpect(jsonPath("$.data.historyElements").isNotEmpty())
			.andDo(print());

	}

	@Test
	@Order(3)
	public void testGetHistoryIdNotFound() throws Exception {
		long historyId = 20000L;
		mockMvc.perform(get("/api/history/" + historyId)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("해당 활동 내역을 찾을 수 없습니다."))
			.andDo(print());
	}

	@Test
	@Order(4)
	public void testGetHistoryUserNotFound() throws Exception {
		long historyId = historyRepository.findByUser(testUser1).get(0).getHistoryId();
		mockMvc.perform(get("/api/history/" + historyId)
				.header("Authorization", "Bearer " + token2)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("해당 활동 내역을 찾을 수 없습니다."))
			.andDo(print());
	}

	@Test
	@Order(5)
	public void testCreateHistory() throws Exception {
		HistoryDTO test = HistoryDTO.builder()
			.historyName("New History")
			.historyElements(Map.of("type", "transfer", "amount", "1000"))
			.build();
		String testJson = objectMapper.writeValueAsString(test);
		mockMvc.perform(post("/api/history")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("활동내역 등록 성공"))
			.andExpect(jsonPath("$.data.historyId").isNotEmpty())
			.andDo(print());

		List<History> histories = historyRepository.findByUser(testUser1);
		assertThat(histories).hasSize(3);
		assertThat(histories.get(2).getHistoryName()).isEqualTo("New History");
	}

	@Test
	@Order(6)
	public void testGetHistoryElementsInvalidJson() {
		History history = new History();
		history.setHistoryElements("{\"key1\":\"value1\",\"key2\":\"value2\"");

		Exception exception = assertThrows(RuntimeException.class, () -> HistoryService.getHistoryElements(history));

		assertTrue(exception.getMessage().contains("올바른 JSON 형식이 아닙니다."));
	}

	@Test
	@Order(7)
	public void testCreateHistoryObjectMapper() {
		Object testObject = new Object();

		HistoryDTO historyDTO = HistoryDTO.builder()
			.historyName("Test History with ObjectMapper")
			.historyElements(Map.of("test", testObject))
			.activityDate(LocalDateTime.now())
			.build();

		Exception exception = assertThrows(RuntimeException.class, () -> historyService.createHistory(historyDTO, "Bearer " + token));

		assertNotNull(exception.getMessage());
	}

	@AfterAll
	public void afterAll() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
		consultationRepository.deleteAll();
		shortcutRepository.deleteAll();
		userRepository.deleteAll();
	}
}
