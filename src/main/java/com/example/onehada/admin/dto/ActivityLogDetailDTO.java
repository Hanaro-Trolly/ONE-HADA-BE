package com.example.onehada.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityLogDetailDTO {
	private LocalDateTime timestamp;
	private String details;
}