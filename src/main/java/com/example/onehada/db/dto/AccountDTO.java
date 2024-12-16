package com.example.onehada.db.dto;

import java.time.LocalDateTime;

import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class AccountDTO {
	@Data
	@AllArgsConstructor
	public static class accountsDTO {
		private Long accountId;
		private String accountType;
		private String accountName;
		private String accountNumber;
		private Long balance;
		private String bank;
	}

	@Getter
	@Builder
	public static class accountExistDTO{
		private String userName;
		private Long accountId;
		private String bank;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class accountDetailDTO{
		private Long userId;
		private Long accountId;
		private String accountName;
		private String accountNumber;
		private String accountType;
		private Long balance;
		private String bank;

		// 잔액 업데이트 메서드 (setter 사용 지양)
		public void updateBalance(Long amount) {
			this.balance += amount;
		}
		public Account toEntity(User user) {
			return Account.builder()
				.user(user)
				.accountId(this.accountId)
				.accountName(this.accountName)
				.accountType(this.accountType)
				.accountNumber(this.accountNumber)
				.balance(this.balance)
				.bank(this.bank)
				.build();
		}
	}
	@Getter
	@Builder
	public static class accountTransferRequest {
		@JsonProperty("from_account_id")
		private Long fromAccountId;

		@JsonProperty("to_account_id")
		private Long toAccountId;

		private Long amount;

		@JsonProperty("sender_message")
		private String senderMessage;

		@JsonProperty("receiver_message")
		private String receiverMessage;
	}

	@Getter
	@Builder
	public static class accountTransferResponse {
		private Long amount;
		private LocalDateTime transactionDate;
		private String senderView;
		private String receiverView;
	}
}
