package com.example.onehada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import com.example.onehada.customer.agent.Agent;
import com.example.onehada.customer.agent.AgentRepository;
import com.example.onehada.customer.consultation.Consultation;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.customer.transaction.TransactionRepository;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ConsultationControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private AgentRepository agentRepository;

	@Autowired
	private UserRepository userRepository;


	private String token;

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

		Agent agent = Agent.builder()
			.agentName("testAgent1")
			.agentPw("123456")
			.agentEmail("testagent1@example.com")
			.build();
		agentRepository.save(agent);

		authService.login(AuthRequestDTO.builder()
			.email(testUser1.getUserEmail())
			.simplePassword(testUser1.getSimplePassword())
			.build());

		this.token = jwtService.generateAccessToken(testUser1.getUserEmail(), testUser1.getUserId());

		Consultation testConsultation1 = Consultation.builder()
			.user(testUser1)
			.consultationDate(LocalDateTime.now())
			.consultationTitle("Test Consultation title")
			.consultationContent("Test Consultation content")
			.agent(agent)
			.build();
		consultationRepository.save(testConsultation1);

		Consultation testConsultation2 = Consultation.builder()
			.user(testUser1)
			.consultationDate(LocalDateTime.now())
			.consultationTitle("Test Consultation title2")
			.consultationContent("Test Consultation content2")
			.agent(agent)
			.build();
		consultationRepository.save(testConsultation2);
	}

	@Test
	@Order(1)
	public void testGetConsultations() throws Exception {
		mockMvc.perform(get("/api/consultations")
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.consultations[0].consultationTitle").value("Test Consultation title2"))
			.andDo(print());
	}
}
