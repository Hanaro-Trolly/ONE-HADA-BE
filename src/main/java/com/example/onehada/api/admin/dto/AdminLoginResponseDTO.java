package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginResponseDTO {
	private Long id;
	private String agentName;
	private String agentEmail;
}
