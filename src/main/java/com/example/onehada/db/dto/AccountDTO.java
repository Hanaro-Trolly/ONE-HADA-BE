package com.example.onehada.db.dto;

import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


public class AccountDTO {
	@Data
	@AllArgsConstructor
	public static class accountsDTO {
		private long accountId;
		private String accountName;
		private String accountNumber;
		private long balance;
		private String bank;
	}

	@Data
	@AllArgsConstructor
	@Builder
	public static class accountDetailDTO{
		private long userId;
		private long accountId;
		private String accountName;
		private String accountNumber;
		private String accountType;
		private long balance;
		private String bank;
	}
}
