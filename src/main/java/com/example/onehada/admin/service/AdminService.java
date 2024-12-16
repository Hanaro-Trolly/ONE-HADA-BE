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
import com.example.onehada.admin.exception.AgentNotFoundException;
import com.example.onehada.admin.exception.InvalidCredentialsException;
import com.example.onehada.admin.exception.UserNotFoundException;
import com.example.onehada.customer.agent.Agent;
import com.example.onehada.customer.agent.AgentRepository;
import com.example.onehada.customer.consultation.Consultation;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.History;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public AdminLoginResponseDTO login(AdminLoginRequestDTO request) throws InvalidCredentialsException {
		Agent agent = agentRepository.findByAgentEmailAndAgentPw(
				request.getAgentEmail(),
				request.getAgentPw()
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
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(UserNotFoundException::new);

		Agent agent = agentRepository.findById(request.getAgentId())
			.orElseThrow(AgentNotFoundException::new);

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