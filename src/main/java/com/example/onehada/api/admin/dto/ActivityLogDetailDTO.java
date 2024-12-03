package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityLogDetailDTO {
	private LocalDateTime timestamp;
	private String details;
}
