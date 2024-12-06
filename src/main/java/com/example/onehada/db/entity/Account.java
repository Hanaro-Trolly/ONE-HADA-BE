package com.example.onehada.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

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
	@Builder.Default
	private long balance = 0;
}
