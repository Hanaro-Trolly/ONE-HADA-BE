package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long transaction_id;

	@ManyToOne
	@JoinColumn(name = "sender_account_id", nullable = false)
	private Account sender_account;

	@ManyToOne
	@JoinColumn(name = "receiver_account_id", nullable = false)
	private Account receiver_account;

	@Column(nullable = false)
	private long amount;

	@Column(length = 31)
	private String sender_name;

	@Column(length = 31)
	private String receiver_name;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime transaction_date;
}
