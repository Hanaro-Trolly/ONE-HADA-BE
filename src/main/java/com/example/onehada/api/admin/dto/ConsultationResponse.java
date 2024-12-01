package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ConsultationResponse {
	private String userId;
	private List<ConsultationDetail> consultations;
}
