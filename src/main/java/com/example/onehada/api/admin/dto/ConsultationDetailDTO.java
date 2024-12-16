package com.example.onehada.api.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConsultationDetailDTO {
	private String id;
	private String agentId;
	private String consultationTitle;
	private String consultationContent;
	private LocalDateTime consultationDate;
}
