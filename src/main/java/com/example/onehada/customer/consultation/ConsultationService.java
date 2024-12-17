package com.example.onehada.customer.consultation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;

@Service
public class ConsultationService {
	private final ConsultationRepository consultationRepository;
	private final JwtService jwtService;

	private Long getUserIdFromToken(String token) {
		String accessToken = token.replace("Bearer ", "");
		return jwtService.extractUserId(accessToken);
	}

	public ConsultationService(ConsultationRepository consultationRepository, JwtService jwtService) {
		this.consultationRepository = consultationRepository;
		this.jwtService = jwtService;
	}

	public List<ConsultationDTO> getConsultations(String token) {
		Long userId = getUserIdFromToken(token);
		List<Consultation> consultations = consultationRepository.findByUserUserId(userId);

		return consultations.stream().map(consultation -> ConsultationDTO.builder()
			.userId(consultation.getUser().getUserId())
			.agentId(consultation.getAgent().getAgentId())
			.consultationId(consultation.getConsultationId())
			.consultationTitle(consultation.getConsultationTitle())
			.consultationContent(consultation.getConsultationContent())
			.consultationDate(consultation.getConsultationDate())
			.build()).collect(Collectors.toList());
	}
}
