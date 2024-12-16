package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConsultationResponseDTO {
	private Long userId;
	private List<ConsultationDetailDTO> consultations;
}
