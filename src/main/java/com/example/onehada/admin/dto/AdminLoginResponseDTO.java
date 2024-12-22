package com.example.onehada.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponseDTO {
	private Long id;
	private String agentName;
	private String agentEmail;
}
