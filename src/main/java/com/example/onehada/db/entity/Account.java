package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@Column(length = 127, nullable = false)
	private String account_name;

	@Column(length = 31, nullable = false)
	private String bank;

	@Column(length = 31, nullable = false)
	private String account_number;

	@Column(length = 31, nullable = false)
	private String account_type;

	@Column(nullable = false)
	private long balance = 0;
}
