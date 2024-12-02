package com.example.onehada.api.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentResponse {
	private String id;
	private String agent_name;
	private String agent_email;
	private String agent_pw;
}
