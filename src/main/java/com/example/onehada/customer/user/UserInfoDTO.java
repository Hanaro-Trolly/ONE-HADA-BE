package com.example.onehada.customer.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
	private Long userId;
	private String userName;
	private String userEmail;
	private String userPhone;
	private String userAddress;
	private String userBirth;
	private LocalDate userRegister;
	private String userGender;
}
