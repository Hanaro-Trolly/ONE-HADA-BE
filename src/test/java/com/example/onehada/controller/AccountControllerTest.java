package com.example.onehada.controller;

import com.example.onehada.auth.dto.AuthRequestDTO;
import com.example.onehada.auth.service.AuthService;
import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.account.AccountService;
import com.example.onehada.customer.account.Account;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AccountService accountService;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AuthService authService;

	@Autowired
	private ShortcutRepository shortcutRepository;
	@Autowired
	HistoryRepository historyRepository;
	@Autowired
	ConsultationRepository consultationRepository;

	private String token;
	private String tokenWithoutBearer;
	private User testUser1, testUser2;
	private Account testAccount1, testAccount2;

	@BeforeAll
	public void setUp() {
		accountRepository.deleteAll();
		shortcutRepository.deleteAll();
		historyRepository.deleteAll();
		consultationRepository.deleteAll();
		userRepository.deleteAll();

		// 테스트용 사용자 생성 및 JWT 토큰 생성
		testUser1 = User.builder()
			.userName("testuser1")
			.userEmail("testuser1@example.com")
			.userGender("M")
			.phoneNumber("01012345678")
			.userAddress("서울시 강남구")
			.userBirth("19900101")
			.simplePassword("12345678")
			.build();
		userRepository.save(testUser1);

		testUser2 = User.builder()
			.userName("testuser2")
			.userEmail("testuser2@example.com")
			.userGender("F")
			.phoneNumber("01087654321")
			.userAddress("서울시 송파구")
			.userBirth("19920202")
			.simplePassword("87654321")
			.build();
		userRepository.save(testUser2);

		testAccount1 = Account.builder()
			.user(testUser1)
			.accountName("테스트계좌1")
			.bank("하나은행")
			.accountNumber("111-1111-1111")
			.accountType("기본")
			.balance(100000L)
			.build();
		accountRepository.save(testAccount1);

		testAccount2 = Account.builder()
			.user(testUser2)
			.accountName("테스트계좌2")
			.bank("하나은행")
			.accountNumber("111-1111-1112")
			.accountType("기본")
			.balance(50000L)
			.build();
		accountRepository.save(testAccount2);

		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());
		// JWT 토큰 생성
		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());
	}

	//계좌 조회
	@Test
	@Order(1)
	public void testGetUserAccounts() throws Exception {
		mockMvc.perform(get("/api/accounts")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("계좌정보를 불러왔습니다."))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].accountName").value(testAccount1.getAccountName()));
	}

	@Test
	@Order(2)
	public void testGetAccountById() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", testAccount1.getAccountId())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("단일 계좌 정보를 성공적으로 가져왔습니다."))
			.andExpect(jsonPath("$.data.accountName").value(testAccount1.getAccountName()));
	}

	@Test
	@Order(3)
	public void testGetAccountByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", 999999L)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("해당 계좌를 찾을 수 없습니다. ID: 999999"));
	}

	@Test
	@Order(4)
	public void testGetAccountByIdForbiddenUser() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", testAccount2.getAccountId())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.status").value("FORBIDDEN"))
			.andExpect(jsonPath("$.message").value("User does not have access to this account"));
	}

	// @Test
	// @Order(5)
	// public void testCheckAccountExistence() throws Exception {
	// 	mockMvc.perform(get("/api/accounts/exist/{accountNumber}", testAccount1.getAccountNumber())
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("200"))
	// 		.andExpect(jsonPath("$.message").value("계좌 존재 여부 확인 성공"))
	// 		.andExpect(jsonPath("$.data.accountNumber").value(testAccount1.getAccountNumber()));
	// }
	//
	// @Test
	// @Order(6)
	// public void testCheckAccountExistenceNotFound() throws Exception {
	// 	mockMvc.perform(get("/api/accounts/exist/{accountNumber}", "999-9999-9999")
	// 			.header("Authorization", "Bearer " + token)
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("200"))
	// 		.andExpect(jsonPath("$.message").value("계좌 존재 여부 확인 성공"))
	// 		.andExpect(jsonPath("$.data").doesNotExist());
	// }

	@AfterAll
	public void AfterAll() {
		accountRepository.deleteAll();
		userRepository.deleteAll();
	}
}
