package com.example.onehada.admin;

import com.example.onehada.admin.dto.AdminLoginRequestDTO;
import com.example.onehada.admin.dto.ConsultationCreateRequestDTO;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional

//@WithMockUser(username = "admin", roles = "ADMIN") // jwt 적용 전
public class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AgentRepository agentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private ShortcutRepository shortcutRepository;

	@Autowired
	private HistoryRepository historyRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	private Agent testAgent;
	private User testUser;

	@BeforeEach
	void setUp() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
		shortcutRepository.deleteAll();
		historyRepository.deleteAll();
		consultationRepository.deleteAll();
		agentRepository.deleteAll();
		userRepository.deleteAll();

		// 테스트 상담원 생성
		testAgent = agentRepository.save(Agent.builder()
			.agentEmail("test@admin.com")
			.agentName("테스트 상담원")
			.agentPw("password123")
			.build());

		// 테스트 사용자 생성
		testUser = userRepository.save(User.builder()
			.userEmail("user@test.com")
			.userName("테스트 사용자")
			.userGender("M")
			.phoneNumber("01012345678")
			.userBirth("19900101")
			.simplePassword("1234")
			.build());
	}
	@Test
	void testAgentAndUserSavedInDB() {
		// 상담원이 DB에 저장되었는지 확인
		assertNotNull(testAgent.getAgentId(), "Agent ID should not be null after saving");
		assertEquals("test@admin.com", testAgent.getAgentEmail(), "Agent email should match the expected value");
		assertEquals("테스트 상담원", testAgent.getAgentName(), "Agent name should match the expected value");

		// 사용자 정보가 DB에 저장되었는지 확인
		assertNotNull(testUser.getUserId(), "User ID should not be null after saving");
		assertEquals("user@test.com", testUser.getUserEmail(), "User email should match the expected value");
		assertEquals("테스트 사용자", testUser.getUserName(), "User name should match the expected value");
	}
	@Transactional
	@Test
	void testAgentAndUserSavedInDB2() {
		testAgent = agentRepository.save(testAgent);
		agentRepository.flush();
		assertNotNull(testAgent.getAgentId(), "Agent ID should not be null after saving");
	}


	@Test
	void loginTest() throws Exception {
		AdminLoginRequestDTO request = new AdminLoginRequestDTO();
		request.setAgentEmail("test@admin.com");
		request.setAgentPw("password123");

		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.data.agentEmail").value("test@admin.com"));
	}

	@Test
	void getAgentsTest() throws Exception {
		// 상담원 목록 조회
		mockMvc.perform(get("/api/admin/agent")
						.param("email", "test@admin.com"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.data").isArray()) // 데이터가 배열인지 확인
				.andExpect(jsonPath("$.data.length()").value(1)) // 배열의 길이가 1인지 확인
				.andExpect(jsonPath("$.data[0].agentEmail").value("test@admin.com"));
	}


	@Test
	void createAndGetConsultationTest() throws Exception {
		ConsultationCreateRequestDTO request = new ConsultationCreateRequestDTO();
		request.setAgentId(testAgent.getAgentId());
		request.setUserId(testUser.getUserId());
		request.setConsultationTitle("테스트 상담");
		request.setConsultationContent("테스트 상담 내용");
		request.setConsultationDate(LocalDateTime.now());

		// 상담 생성
		mockMvc.perform(post("/api/admin/consultation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(201));

		// 상담 조회
		mockMvc.perform(get("/api/admin/consultation/" + testUser.getUserId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.consultations[0].consultationTitle")
				.value("테스트 상담"));
	}

	@Test
	void getUserTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/" + testUser.getUserId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.userName").value("테스트 사용자"))
			.andExpect(jsonPath("$.data.userGender").value("M"))
			.andExpect(jsonPath("$.data.userPhone").value("01012345678"))
			.andExpect(jsonPath("$.data.userBirth").value("19900101"));
	}

	@Test
	void loginFailTest() throws Exception {
		AdminLoginRequestDTO request = new AdminLoginRequestDTO();
		request.setAgentEmail("wrong@admin.com");
		request.setAgentPw("wrongpassword");

		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("아이디 혹은 비밀번호가 잘못 되었습니다."));
	}

	@Test
	void createConsultationInvalidUserTest() throws Exception {
		ConsultationCreateRequestDTO request = new ConsultationCreateRequestDTO();
		request.setAgentId(testAgent.getAgentId());
		request.setUserId(999999L); // 존재하지 않는 사용자 ID
		request.setConsultationTitle("테스트 상담");
		request.setConsultationContent("테스트 상담 내용");
		request.setConsultationDate(LocalDateTime.now());

		mockMvc.perform(post("/api/admin/consultation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(404))
			.andExpect(jsonPath("$.status").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("상담데이터 추가 중 유저를 찾을 수 없습니다."));
	}

	@Test
	void searchUsersByNameTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/search")
				.param("userName", "테스트"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("사용자 검색 성공"))
			.andExpect(jsonPath("$.data[0].userName").value("테스트 사용자"))
			.andExpect(jsonPath("$.data[0].userBirth").value("19900101"));
	}

	@Test
	void searchUsersByBirthTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/search")
				.param("userBirth", "19900101"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data[0].userName").value("테스트 사용자"))
			.andExpect(jsonPath("$.data[0].userBirth").value("19900101"));
	}

	@Test
	void searchUsersByNameAndBirthTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/search")
				.param("userName", "테스트")
				.param("userBirth", "19900101"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data[0].userName").value("테스트 사용자"))
			.andExpect(jsonPath("$.data[0].userBirth").value("19900101"));
	}

	@Test
	void searchUsersWithNoParamsTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/search"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("검색 조건을 입력해주세요."));
	}

	@Test
	void searchUsersNoResultTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/search")
				.param("userName", "존재하지않는사용자"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void getConsultationListTest() throws Exception {
		// 테스트용 상담 데이터 생성
		Consultation consultation = consultationRepository.save(Consultation.builder()
			.agent(testAgent)
			.user(testUser)
			.consultationTitle("테스트 상담")
			.consultationContent("테스트 상담 내용")
			.consultationDate(LocalDateTime.now())
			.build());

		// 상담 목록 조회 테스트
		mockMvc.perform(get("/api/admin/consultationList/" + testAgent.getAgentId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("상담사 상담 내역 조회 성공"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].userId").value(testUser.getUserId()))
			.andExpect(jsonPath("$.data[0].userName").value(testUser.getUserName()))
			.andExpect(jsonPath("$.data[0].lastConsultationTitle").value("테스트 상담"));
	}

	@Test
	void getConsultationListWithInvalidAgentIdTest() throws Exception {
		Long invalidAgentId = 999999L; // 존재하지 않는 상담사 ID

		mockMvc.perform(get("/api/admin/consultationList/" + invalidAgentId))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(500))
			.andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
			.andExpect(jsonPath("$.message").value("Agent not found"));
	}

	@Test
	void getConsultationListEmptyTest() throws Exception {
		// 상담 데이터 없이 조회
		mockMvc.perform(get("/api/admin/consultationList/" + testAgent.getAgentId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("상담사 상담 내역 조회 성공"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
