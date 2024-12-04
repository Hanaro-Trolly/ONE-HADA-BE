package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UserResponseDTO {
	private String id;
	private String userName;
	private String userBirth;
	private String userPhone;
	private String userGender;
}
