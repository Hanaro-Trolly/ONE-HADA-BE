package com.example.onehada.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	private String name;
	private String gender;
	private String birthdate;
	private String phone;
	private String address;
	private String google;
	private String kakao;
	private String naver;
}
