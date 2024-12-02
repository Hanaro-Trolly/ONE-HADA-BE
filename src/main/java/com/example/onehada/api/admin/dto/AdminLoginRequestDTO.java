package com.example.onehada.api.admin.dto;

import lombok.*;

@Getter
@Setter
@Data
public class AdminLoginRequest {
	private String agent_email;
	private String agent_pw;
}
