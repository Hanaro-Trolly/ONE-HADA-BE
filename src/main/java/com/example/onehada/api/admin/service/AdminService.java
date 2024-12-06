package com.example.onehada.api.admin.service;

import com.example.onehada.api.admin.dto.*;
import com.example.onehada.api.admin.exception.AgentNotFoundException;
import com.example.onehada.api.admin.exception.InvalidCredentialsException;
import com.example.onehada.api.admin.exception.UserNotFoundException;
import com.example.onehada.db.entity.*;
import com.example.onehada.db.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public AdminLoginResponseDTO login(AdminLoginRequestDTO request) throws InvalidCredentialsException {
		Agent agent = agentRepository.findByAgentEmailAndAgentPw(
				request.getAgent_email(),
				request.getAgent_pw()
			)
			.orElseThrow(InvalidCredentialsException::new);

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

	public ActivityLogResponseDTO getActivityLogs(Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

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
		UserNotFoundException, AgentNotFoundException {
		User user = userRepository.findById(request.getUser_id())
			.orElseThrow(UserNotFoundException::new);

		Agent agent = agentRepository.findById(request.getAgent_id())
			.orElseThrow(AgentNotFoundException::new);

		Consultation consultation = Consultation.builder()
			.user(user)
			.agent(agent)
			.consultationTitle(request.getConsultation_title())
			.consultationContent(request.getConsultation_content())
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

	public List<UserResponseDTO> searchUsers(String userName, String userBirth) {
		List<User> users;

		if (userName != null && userBirth != null) {
			users = userRepository.findByUserNameContainingAndUserBirth(userName, userBirth);
		} else if (userName != null) {
			users = userRepository.findByUserNameContaining(userName);
		} else {
			users = userRepository.findByUserBirth(userBirth);
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
}
