package com.example.onehada.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTO {
	private long accountId;
	private String accountName;
	private String accountNumber;
	private long balance;
	private String bank;
}
