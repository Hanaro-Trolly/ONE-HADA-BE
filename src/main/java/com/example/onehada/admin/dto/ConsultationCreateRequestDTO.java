package com.example.onehada.admin.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultationCreateRequestDTO {
	private Long agentId;
	private Long userId;
	private String consultationTitle;
	private String consultationContent;
	private LocalDateTime consultationDate;
}
