package com.example.onehada.db.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TransactionDTO {
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class transactionDTO {
		private Long fromAccountId;
		private Long toAccountId;
		private Long amount;
		private String transactionType;
		private String senderMessage;
		private String receiverMessage;
		private LocalDateTime transactionDate;
	}
}
