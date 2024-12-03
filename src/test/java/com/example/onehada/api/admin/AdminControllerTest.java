package com.example.onehada.api.admin;

import com.example.onehada.api.admin.dto.*;
import com.example.onehada.db.entity.*;
import com.example.onehada.db.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN") // jwt 적용 전
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
	private HistoryRepository historyRepository;

	private Agent testAgent;
	private User testUser;

	@BeforeEach
	void setUp() {
		// 기존 데이터 정리
		accountRepository.deleteAll();
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
	void loginTest() throws Exception {
		AdminLoginRequestDTO request = new AdminLoginRequestDTO();
		request.setAgent_email("test@admin.com");
		request.setAgent_pw("password123");

		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.data.agent_email").value("test@admin.com"));
	}

	@Test
	void getAgentsTest() throws Exception {
		// 상담원 목록 조회
		mockMvc.perform(get("/api/admin/agent")
				.param("email", "test@admin.com"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data[0].agent_email").value("test@admin.com"));
	}

	@Test
	void createAndGetConsultationTest() throws Exception {
		ConsultationCreateRequestDTO request = new ConsultationCreateRequestDTO();
		request.setAgent_id(testAgent.getAgentId());
		request.setUser_id(testUser.getUserId());
		request.setConsultation_title("테스트 상담");
		request.setConsultation_content("테스트 상담 내용");
		request.setConsultation_date(LocalDateTime.now());

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
			.andExpect(jsonPath("$.data.consultations[0].consultation_title")
				.value("테스트 상담"));
	}

	@Test
	void getUserTest() throws Exception {
		mockMvc.perform(get("/api/admin/user/" + testUser.getUserId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.user_name").value("테스트 사용자"))
			.andExpect(jsonPath("$.data.user_gender").value("M"))
			.andExpect(jsonPath("$.data.user_phone").value("01012345678"))
			.andExpect(jsonPath("$.data.user_birth").value("19900101"));
	}


	@Test
	void loginFailTest() throws Exception {
		AdminLoginRequestDTO request = new AdminLoginRequestDTO();
		request.setAgent_email("wrong@admin.com");
		request.setAgent_pw("wrongpassword");

		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 잘못되었습니다."));
	}

	@Test
	void createConsultationInvalidUserTest() throws Exception {
		ConsultationCreateRequestDTO request = new ConsultationCreateRequestDTO();
		request.setAgent_id(testAgent.getAgentId());
		request.setUser_id(999999L); // 존재하지 않는 사용자 ID
		request.setConsultation_title("테스트 상담");
		request.setConsultation_content("테스트 상담 내용");
		request.setConsultation_date(LocalDateTime.now());

		mockMvc.perform(post("/api/admin/consultation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.status").value("USER_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("상담 데이터 추가 실패"));
	}
}
