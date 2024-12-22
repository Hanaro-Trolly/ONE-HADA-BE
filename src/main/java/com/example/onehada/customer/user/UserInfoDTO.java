package com.example.onehada.customer.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
