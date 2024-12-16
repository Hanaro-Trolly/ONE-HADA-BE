package com.example.onehada.db.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TransactionDTO {
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class transactionDTO{
		private Long transactionId;
		private Long amount;
		private Long postSenderBalance;
		private Long postReceiverBalance;
		private String transactionType;
		private LocalDateTime transactionDateTime;
		private String senderView;
		private String receiverView;
		// DTO 변환 메서드
		public static transactionDTO fromEntity(Transaction transaction, Account account) {
			// 거래 유형 계산
			String transactionType = transaction.getSenderAccount().equals(account) ? "출금" : "입금";

			return transactionDTO.builder()
				.transactionId(transaction.getTransactionId())
				.amount(transaction.getAmount())
				.postSenderBalance(transaction.getPostSenderBalance())
				.postReceiverBalance(transaction.getPostReceiverBalance())
				.transactionType(transactionType)
				.senderView(transaction.getSenderName())
				.receiverView(transaction.getReceiverName())
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
		private int page;
		private int limit;
	}
}
