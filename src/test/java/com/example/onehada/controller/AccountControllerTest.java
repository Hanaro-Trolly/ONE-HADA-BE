package com.example.onehada.controller;

import com.example.onehada.api.auth.dto.AuthRequestDTO;
import com.example.onehada.api.auth.dto.AuthResponseDTO;
import com.example.onehada.api.service.AccountService;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.AccountRepository;
import com.example.onehada.db.repository.ConsultationRepository;
import com.example.onehada.db.repository.HistoryRepository;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	private String accessToken;

	@BeforeEach
	public void setUp() throws Exception {
		consultationRepository.deleteAll();
		historyRepository.deleteAll();
		accountRepository.deleteAll();
		userRepository.deleteAll();


		// 2. 테스트용 유저 생성
		User testUser = User.builder()
			.userEmail("test@test.com")
			.userName("테스트")
			.simplePassword("1234")
			.userGender("M")
			.phoneNumber("01012345678")
			.userBirth("19900101")
			.build();

		User savedUser = userRepository.save(testUser);

		// 3. 테스트용 계좌 생성
		Account testAccount = Account.builder()
			.accountName("입출금")
			.accountNumber("123456789")
			.balance(1000000)
			.bank("은행A")
			.user(savedUser)
			.accountType("normal")
			.build();

		accountRepository.save(testAccount);

		// 4. 로그인 요청
		AuthRequestDTO loginRequest = AuthRequestDTO.builder()
			.email("test@test.com")
			.simplePassword("1234")
			.build();

		MvcResult result = mockMvc.perform(post("/api/cert/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		// 5. 토큰 저장
		AuthResponseDTO response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			AuthResponseDTO.class
		);

		accessToken = "Bearer " + response.getAccessToken();
	}

	@Test
	public void testGetUserAccounts_Success() throws Exception {
		// 생성된 계좌의 실제 ID를 가져옴
		Account savedAccount = accountRepository.findAccountsByUserUserEmail("test@test.com").get(0);
		// When & Then
		mockMvc.perform(get("/api/accounts")
				.header("Authorization", accessToken))
			.andDo(print())  // 실패 시 응답 내용 출력
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("계좌정보를 불러왔습니다."))
			.andExpect(jsonPath("$.data[0].accountId").value(savedAccount.getAccountId()))
			.andExpect(jsonPath("$.data[0].accountName").value("입출금"))
			.andExpect(jsonPath("$.data[0].accountNumber").value("123456789"))
			.andExpect(jsonPath("$.data[0].balance").value(1000000))
			.andExpect(jsonPath("$.data[0].bank").value("은행A"));
	}
}
