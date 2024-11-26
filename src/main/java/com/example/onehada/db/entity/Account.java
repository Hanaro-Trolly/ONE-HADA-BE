package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long accountId;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@Column(length = 127, nullable = false)
	private String accountName;

	@Column(length = 31, nullable = false)
	private String bank;

	@Column(length = 31, nullable = false)
	private String accountNumber;

	@Column(length = 31, nullable = false)
	private String accountType;

	@Column(nullable = false)
	private long balance = 0;
}
