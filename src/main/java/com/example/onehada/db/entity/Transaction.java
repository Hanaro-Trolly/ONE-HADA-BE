package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long transactionId;

	@ManyToOne
	@JoinColumn(name = "sender_account_id", nullable = false)
	private Account senderAccount;

	@ManyToOne
	@JoinColumn(name = "receiver_account_id", nullable = false)
	private Account receiverAccount;

	@Column(nullable = false)
	private long amount;

	@Column(length = 31)
	private String senderName;

	@Column(length = 31)
	private String receiverName;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime transactionDate;
}
