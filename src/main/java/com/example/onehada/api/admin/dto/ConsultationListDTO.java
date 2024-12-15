package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class ConsultationListDTO {
	private Long userId;
	private String userName;
	private LocalDateTime lastConsultationDate;
	private String lastConsultationTitle;
}
