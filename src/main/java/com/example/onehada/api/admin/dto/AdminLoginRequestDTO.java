package com.example.onehada.api.admin.dto;

import lombok.*;

@Getter
@Setter
@Data
public class AdminLoginRequestDTO {
	private String agentEmail;
	private String agentPw;
}
