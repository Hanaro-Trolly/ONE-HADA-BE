package com.example.onehada.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
	private String name;
	private String gender;
	private String birth;
	private String phone;
	private String address;
	private String google;
	private String kakao;
	private String naver;
	private String simplePassword;
}
