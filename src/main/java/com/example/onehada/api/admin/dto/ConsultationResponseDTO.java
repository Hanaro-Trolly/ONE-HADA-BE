package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConsultationResponseDTO {
	private Long userId;
	private List<ConsultationDetailDTO> consultations;
}
