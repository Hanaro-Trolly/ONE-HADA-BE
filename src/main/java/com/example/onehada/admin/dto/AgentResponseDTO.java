package com.example.onehada.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgentResponseDTO {
	private String id;
	private String agentName;
	private String agentEmail;
	private String agentPw;
}
