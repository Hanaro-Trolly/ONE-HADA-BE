package com.example.onehada.db.dto;

import java.time.LocalDateTime;

import com.example.onehada.db.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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

	//정보 은닉성으로 Getter만 사용
	@Getter
	@AllArgsConstructor
	@Builder
	public static class accountTransferDTO{
		private long accountId;
		private String accountName;
		private String accountNumber;
		private long balance;
		private String bank;

		// 잔액 업데이트 메서드 (setter 사용 지양)
		public void updateBalance(long amount) {
			this.balance += amount;
		}
		public Account toEntity() {
			return Account.builder()
				.accountId(this.accountId)
				.accountName(this.accountName)
				.balance(this.balance)
				.build();
		}
	}

	@Getter
	@Builder
	public static class accountTransferRequest {
		private Long fromAccountId;
		private Long toAccountId;
		private Long amount;
		private String senderMessage;
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
