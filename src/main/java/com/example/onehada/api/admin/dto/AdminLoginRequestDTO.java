package com.example.onehada.api.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginRequestDTO {
	private String agentEmail;
	private String agentPw;
}
