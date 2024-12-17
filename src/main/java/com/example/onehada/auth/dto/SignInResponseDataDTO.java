package com.example.onehada.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDataDTO {
	private String accessToken;
	private String refreshToken;
	private String userId;
}
