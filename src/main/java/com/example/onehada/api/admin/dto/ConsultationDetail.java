package com.example.onehada.api.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsultationDetail {
	private String id;
	private String agent_id;
	private String consultation_title;
	private String consultation_content;
	private LocalDateTime consultation_date;
}
