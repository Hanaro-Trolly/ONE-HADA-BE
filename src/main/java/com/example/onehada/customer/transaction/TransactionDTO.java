package com.example.onehada.customer.transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.onehada.customer.account.Account;
import com.example.onehada.customer.transaction.Transaction;

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
		private Long transactionId;
		private String transactionType;
		private Long amount;
		private Long balance;
		private LocalDateTime transactionDateTime;
		private String view;

		// DTO 변환 메서드
		public static transactionDTO fromEntity(Transaction transaction, Account account) {

			// 출금 처리
			if (transaction.getSenderAccount().equals(account)) {
				return transactionDTO.builder()
					.transactionId(transaction.getTransactionId())
					.amount(transaction.getAmount())
					.balance(transaction.getPostSenderBalance())
					.transactionType("출금")
					.view(transaction.getReceiverName())
					.transactionDateTime(transaction.getTransactionDate())
					.build();
			}
			return transactionDTO.builder()
				.transactionId(transaction.getTransactionId())
				.amount(transaction.getAmount())
				.balance(transaction.getPostReceiverBalance())
				.transactionType("입금")
				.view(transaction.getSenderName())
				.transactionDateTime(transaction.getTransactionDate())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class transactionRequest {
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private String transactionType;
		private String keyword;

		public void initDateRange() {
			if (this.startDate == null) {
				this.startDate = LocalDateTime.MIN;
			}
			if (this.endDate == null) {
				this.endDate = LocalDateTime.now();
			}
			this.startDate = this.startDate.toLocalDate().atStartOfDay();
			this.endDate = this.endDate.toLocalDate().atTime(23, 59, 59, 999999999);
		}
	}
}
