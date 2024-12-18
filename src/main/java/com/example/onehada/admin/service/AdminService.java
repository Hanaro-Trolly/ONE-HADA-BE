package com.example.onehada.admin.service;

import com.example.onehada.admin.dto.ActivityLogDetailDTO;
import com.example.onehada.admin.dto.ActivityLogResponseDTO;
import com.example.onehada.admin.dto.AdminLoginRequestDTO;
import com.example.onehada.admin.dto.AdminLoginResponseDTO;
import com.example.onehada.admin.dto.AgentResponseDTO;
import com.example.onehada.admin.dto.ConsultationCreateRequestDTO;
import com.example.onehada.admin.dto.ConsultationCreateResponseDTO;
import com.example.onehada.admin.dto.ConsultationDetailDTO;
import com.example.onehada.admin.dto.ConsultationListDTO;
import com.example.onehada.admin.dto.ConsultationResponseDTO;
import com.example.onehada.admin.dto.UserResponseDTO;
import com.example.onehada.customer.agent.Agent;
import com.example.onehada.customer.agent.AgentRepository;
import com.example.onehada.customer.consultation.Consultation;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.History;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
	private final AgentRepository agentRepository;
	private final UserRepository userRepository;
	private final HistoryRepository historyRepository;
	private final ConsultationRepository consultationRepository;

	public AdminLoginResponseDTO login(AdminLoginRequestDTO request) throws BadRequestException {
		Agent agent = agentRepository.findByAgentEmailAndAgentPw(
				request.getAgentEmail(),
				request.getAgentPw()
			)
			.orElseThrow(() -> new BadRequestException("아이디 혹은 비밀번호가 잘못 되었습니다."));

		return new AdminLoginResponseDTO(
			agent.getAgentId(),
			agent.getAgentName(),
			agent.getAgentEmail()
		);
	}

	public List<AgentResponseDTO> getAgents(String email, String name) {
		List<Agent> agents;
		if (email != null) {
			agents = agentRepository.findByAgentEmailContaining(email);
		} else if (name != null) {
			agents = agentRepository.findByAgentNameContaining(name);
		} else {
			agents = agentRepository.findAll();
		}

		return agents.stream()
			.map(agent -> new AgentResponseDTO(
				String.valueOf(agent.getAgentId()),
				agent.getAgentName(),
				agent.getAgentEmail(),
				agent.getAgentPw()
			))
			.collect(Collectors.toList());
	}

	public ActivityLogResponseDTO getActivityLogs(Long userId) throws NotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("활동로그 조회 중 유저를 찾을 수 없습니다."));

		List<History> histories = historyRepository.findByUser(user);
		List<ActivityLogDetailDTO> logs = histories.stream()
			.map(history -> new ActivityLogDetailDTO(
				history.getActivityDate(),
				history.getHistoryName()
			))
			.collect(Collectors.toList());

		return new ActivityLogResponseDTO(
			userId,
			user.getUserName(),
			logs
		);
	}

	@Transactional
	public ConsultationCreateResponseDTO createConsultation(ConsultationCreateRequestDTO request) throws
		NotFoundException {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NotFoundException("상담데이터 추가 중 유저를 찾을 수 없습니다."));

		Agent agent = agentRepository.findById(request.getAgentId())
			.orElseThrow(() -> new NotFoundException("상담데이터 추가 중 상담사를 찾을 수 없습니다."));

		Consultation consultation = Consultation.builder()
			.user(user)
			.agent(agent)
			.consultationTitle(request.getConsultationTitle())
			.consultationContent(request.getConsultationContent())
			.build();

		consultation = consultationRepository.save(consultation);
		return new ConsultationCreateResponseDTO(String.valueOf(consultation.getConsultationId()));
	}

	public ConsultationResponseDTO getConsultations(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		List<Consultation> consultations = consultationRepository.findByUser(user);
		List<ConsultationDetailDTO> details = consultations.stream()
			.map(consultation -> new ConsultationDetailDTO(
				String.valueOf(consultation.getConsultationId()),
				String.valueOf(consultation.getAgent().getAgentId()),
				consultation.getConsultationTitle(),
				consultation.getConsultationContent(),
				consultation.getConsultationDate()
			))
			.collect(Collectors.toList());

		return new ConsultationResponseDTO(userId, details);
	}

	public UserResponseDTO getUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		return new UserResponseDTO(
			String.valueOf(user.getUserId()),
			user.getUserName(),
			user.getUserBirth(),
			user.getPhoneNumber(),
			user.getUserGender()
		);
	}

	public List<UserResponseDTO> searchUsers(String userName, String userBirth, String userPhone) {
		List<User> users;

		// 빈 문자열을 null로 변환
		userName = (userName != null && userName.trim().isEmpty()) ? null : userName;
		userBirth = (userBirth != null && userBirth.trim().isEmpty()) ? null : userBirth;
		userPhone = (userPhone != null && userPhone.trim().isEmpty()) ? null : userPhone;

		if (userName != null && userBirth != null && userPhone != null) {
			users = userRepository.findByUserNameContainingAndUserBirthAndPhoneNumber(userName, userBirth, userPhone);
		} else if (userName != null && userBirth != null) {
			users = userRepository.findByUserNameContainingAndUserBirth(userName, userBirth);
		} else if (userName != null && userPhone != null) {
			users = userRepository.findByUserNameContainingAndPhoneNumber(userName, userPhone);
		} else if (userBirth != null && userPhone != null) {
			users = userRepository.findByUserBirthAndPhoneNumber(userBirth, userPhone);
		} else if (userName != null) {
			users = userRepository.findByUserNameContaining(userName);
		} else if (userBirth != null) {
			users = userRepository.findByUserBirth(userBirth);
		} else if (userPhone != null) {
			users = userRepository.findByPhoneNumber(userPhone);
		} else {
			return new ArrayList<>();
		}

		return users.stream()
			.map(user -> new UserResponseDTO(
				String.valueOf(user.getUserId()),
				user.getUserName(),
				user.getUserBirth(),
				user.getPhoneNumber(),
				user.getUserGender()
			))
			.collect(Collectors.toList());
	}

	public List<ConsultationListDTO> getConsultationList(Long agentId) {
		Agent agent = agentRepository.findById(agentId)
			.orElseThrow(() -> new RuntimeException("Agent not found"));

		return consultationRepository.findByAgent(agent).stream()
			.collect(Collectors.groupingBy(
				consultation -> consultation.getUser(),
				Collectors.maxBy(Comparator.comparing(Consultation::getConsultationDate))
			))
			.values()
			.stream()
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(consultation -> new ConsultationListDTO(
				consultation.getUser().getUserId(),
				consultation.getUser().getUserName(),
				consultation.getConsultationDate(),
				consultation.getConsultationTitle()
			))
			.sorted(Comparator.comparing(ConsultationListDTO::getLastConsultationDate).reversed())
			.collect(Collectors.toList());
	}
}
