package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UserResponse {
	private String id;
	private String user_name;
	private String user_birth;
	private String user_phone;
	private String user_gender;
}
