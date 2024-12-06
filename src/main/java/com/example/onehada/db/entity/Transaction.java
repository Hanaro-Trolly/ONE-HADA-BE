package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;

	@ManyToOne
	@JoinColumn(name = "sender_account_id", nullable = false)
	private Account senderAccount;

	@ManyToOne
	@JoinColumn(name = "receiver_account_id", nullable = false)
	private Account receiverAccount;

	@Column(nullable = false)
	private Long amount;

	@Column(length = 31)
	private String senderName;

	@Column(length = 31)
	private String receiverName;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime transactionDate;
}
