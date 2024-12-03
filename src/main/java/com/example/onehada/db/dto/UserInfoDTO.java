package com.example.onehada.db.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
	private int user_id;
	private String user_name;
	private String user_email;
	private String user_phone;
	private String user_address;
	private String user_birth;
	private LocalDate user_register;
	private String user_gender;
}
