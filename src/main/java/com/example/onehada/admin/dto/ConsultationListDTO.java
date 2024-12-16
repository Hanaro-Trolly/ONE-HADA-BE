package com.example.onehada.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConsultationListDTO {
	private Long userId;
	private String userName;
	private LocalDateTime lastConsultationDate;
	private String lastConsultationTitle;
}
