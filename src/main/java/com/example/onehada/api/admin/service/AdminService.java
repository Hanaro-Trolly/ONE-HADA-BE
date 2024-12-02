package com.example.onehada.api.admin.service;

import com.example.onehada.api.admin.dto.*;
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

	public AdminLoginResponseDTO login(AdminLoginRequestDTO request) {
		Agent agent = agentRepository.findByAgentEmailAndAgentPw(
				request.getAgent_email(),
				request.getAgent_pw()
			)
			.orElseThrow(() -> new RuntimeException("Invalid credentials"));

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

	public ActivityLogResponseDTO getActivityLogs(String userId) {
		User user = userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new RuntimeException("User not found"));

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
	public ConsultationCreateResponseDTO createConsultation(ConsultationCreateRequestDTO request) {
		User user = userRepository.findById(Integer.parseInt(request.getUser_id()))
			.orElseThrow(() -> new RuntimeException("User not found"));

		Agent agent = agentRepository.findById(Integer.parseInt(request.getAgent_id()))
			.orElseThrow(() -> new RuntimeException("Agent not found"));

		Consultation consultation = Consultation.builder()
			.user(user)
			.agent(agent)
			.consultationTitle(request.getConsultation_title())
			.consultationContent(request.getConsultation_content())
			.build();

		consultation = consultationRepository.save(consultation);
		return new ConsultationCreateResponseDTO(String.valueOf(consultation.getConsultationId()));
	}

	public ConsultationResponseDTO getConsultations(String userId) {
		User user = userRepository.findById(Integer.parseInt(userId))
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

	public UserResponseDTO getUser(String userId) {
		User user = userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new RuntimeException("User not found"));

		return new UserResponseDTO(
			String.valueOf(user.getUserId()),
			user.getUserName(),
			user.getUserBirth(),
			user.getPhoneNumber(),
			user.getUserGender()
		);
	}
}
