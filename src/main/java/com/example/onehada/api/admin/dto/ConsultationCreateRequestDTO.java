package com.example.onehada.api.admin.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Data
public class ConsultationCreateRequestDTO {
	private Long agentId;
	private Long userId;
	private String consultationTitle;
	private String consultationContent;
	private LocalDateTime consultationDate;
}
