package com.example.onehada.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.ConsultationDTO;
import com.example.onehada.db.entity.Consultation;
import com.example.onehada.db.repository.ConsultationRepository;
import com.example.onehada.exception.NotFoundException;

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
		if (consultations.isEmpty()) {
			throw new NotFoundException("상담내역이 존재하지 않습니다.");
		}

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
