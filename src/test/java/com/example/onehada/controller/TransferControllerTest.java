package com.example.onehada.controller;

import static java.time.LocalDateTime.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.After;
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

import com.example.onehada.api.auth.dto.AuthRequest;
import com.example.onehada.api.auth.service.AuthService;
import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.dto.TransactionDTO;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.AccountRepository;
import com.example.onehada.db.repository.TransactionRepository;
import com.example.onehada.db.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthService authService;

	private String token;
	User testUser1, testUser2;
	Account testFromAccount, testToAccount;

	@BeforeAll
	public void setUp() {
		accountRepository.deleteAll();
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

		testFromAccount = Account.builder()
			.user(testUser1)
			.accountName("테스트계좌1")
			.bank("하나은행")
			.accountNumber("111-1111-1111")
			.accountType("기본")
			.balance(100000L)
			.build();
		accountRepository.save(testFromAccount);

		testToAccount = Account.builder()
			.user(testUser2)
			.accountName("테스트계좌2")
			.bank("하나은행")
			.accountNumber("111-1111-1112")
			.accountType("기본")
			.balance(50000L)
			.build();
		accountRepository.save(testToAccount);

		authService.login(AuthRequest.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());
		// JWT 토큰 생성
		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());
	}

	// 계좌 이체 테스트
	@Test
	@Order(1)
	public void testTransferSuccess() throws Exception {
		AccountDTO.accountTransferRequest transferRequest = AccountDTO.accountTransferRequest.builder()
			.fromAccountId(testFromAccount.getAccountId())
			.toAccountId(testToAccount.getAccountId())
			.amount(5000L)
			.senderMessage("보내는 메시지")
			.receiverMessage("받는 메시지")
			.build();

		mockMvc.perform(post("/api/transaction/transfer")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(transferRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("계좌 이체 성공"))
			.andExpect(jsonPath("$.data").exists());
	}

	// 계좌 잔액 부족으로 인한 이체 실패 테스트
	@Test
	@Order(2)
	public void testTransferInsufficientBalance() throws Exception {
		AccountDTO.accountTransferRequest transferRequest = AccountDTO.accountTransferRequest.builder()
			.fromAccountId(testFromAccount.getAccountId())
			.toAccountId(testToAccount.getAccountId())
			.amount(200000L)
			.senderMessage("보내는 메시지")
			.receiverMessage("받는 메시지")
			.build();

		mockMvc.perform(post("/api/transaction/transfer")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(transferRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("잔액 부족"));
	}
	// 계좌 정보가 존재하지 않는 경우 테스트
	@Test
	@Order(3)
	public void testTransferAccountNotFound() throws Exception {
		// 존재하지 않는 계좌로 이체 시도
		AccountDTO.accountTransferRequest transferRequest = AccountDTO.accountTransferRequest.builder()
			.fromAccountId(testFromAccount.getAccountId())
			.toAccountId(999999L)
			.amount(200000L)
			.senderMessage("보내는 메시지")
			.receiverMessage("받는 메시지")
			.build();

		mockMvc.perform(post("/api/transaction/transfer")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(transferRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("계좌를 찾을 수 없습니다."));
	}

	// 거래 내역 조회 테스트
	@Test
	@Order(4)
	public void testGetTransactions() throws Exception {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		LocalDateTime startDate = LocalDateTime.parse("2024-01-01T00:00:00");
		TransactionDTO.transactionRequest request = TransactionDTO.transactionRequest.builder()
			.startDate(startDate)
			.endDate(now())
			.transactionType("출금")
			.keyword("")
			.page(1)
			.limit(5)
			.build();
		// 거래 내역 조회 요청
		mockMvc.perform(get("/api/transaction/{accountId}", testToAccount.getAccountId())  // accountId는 테스트용 계좌의 ID
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("거래 내역을 불러왔습니다."));
	}

	// 예외 처리 테스트: 잘못된 날짜 범위
	@Test
	@Order(5)
	public void testGetTransactionsInvalidDateRange() throws Exception {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		LocalDateTime startDate = LocalDateTime.parse("2200-01-01T00:00:00");
		// 잘못된 날짜 범위
		TransactionDTO.transactionRequest request = TransactionDTO.transactionRequest.builder()
			.startDate(startDate)
			.endDate(now())
			.transactionType("출금")
			.keyword("")
			.page(1)
			.limit(5)
			.build();

		mockMvc.perform(get("/api/transaction/{accountId}", testToAccount.getAccountId())
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("시작 날짜가 종료 날짜보다 클 수 없습니다."));
	}

	@Test
	@Order(6)
	public void testGetTransactionsNotFoundAccount() throws Exception {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		LocalDateTime startDate = LocalDateTime.parse("2024-01-01T00:00:00");
		// 잘못된 날짜 범위
		TransactionDTO.transactionRequest request = TransactionDTO.transactionRequest.builder()
			.startDate(startDate)
			.endDate(now())
			.transactionType("출금")
			.keyword("")
			.page(1)
			.limit(5)
			.build();

		mockMvc.perform(get("/api/transaction/{accountId}", 99999L)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("존재하지 않는 계좌 ID입니다."));
	}

	@AfterAll
	public void AfterAll() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
		userRepository.deleteAll();
	}

}