package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConsultationResponseDTO {
	private String userId;
	private List<ConsultationDetailDTO> consultations;
}
