package com.example.onehada.db.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@Column(length = 127, nullable = false)
	private String userName;

	@Column(length = 1, nullable = false)
	private String userGender;

	@Column(length = 127, nullable = false)
	private String userEmail;

	@Column(length = 20, nullable = false)
	private String phoneNumber;

	private String userAddress;

	@Column(length = 8, nullable = false)
	private String userBirth;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDate userRegisteredDate;

	private Long userGoogleId;
	private Long userKakaoId;
	private Long userNaverId;

	@Column(length = 8, nullable = false)
	private String simplePassword;
}
