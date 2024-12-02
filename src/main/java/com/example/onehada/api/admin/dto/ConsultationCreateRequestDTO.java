package com.example.onehada.api.admin.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Data
public class ConsultationCreateRequestDTO {
	private String id;
	private String agent_id;
	private String user_id;
	private String consultation_title;
	private String consultation_content;
	private LocalDateTime consultation_date;
}
