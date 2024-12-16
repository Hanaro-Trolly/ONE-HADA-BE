package com.example.onehada.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDTO {
	private int code;
	private String status;
	private String message;
	private SignInResponseDataDTO data;
}
