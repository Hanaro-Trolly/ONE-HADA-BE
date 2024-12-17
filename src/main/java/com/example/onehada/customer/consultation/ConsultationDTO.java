package com.example.onehada.customer.consultation;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsultationDTO {
	private Long consultationId;
	private Long userId;
	private Long agentId;
	private String consultationTitle;
	private String consultationContent;
	private LocalDateTime consultationDate;

}
