package com.example.onehada.customer.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserUpdateDTO {

	private String userPhone;
	private String userAddress;
}
