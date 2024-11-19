package com.example.onehada.db.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;

	@Column(length = 127, nullable = false)
	private String user_name;

	@Column(length = 1, nullable = false)
	private String user_gender;

	@Column(length = 127, nullable = false)
	private String user_email;

	@Column(length = 20, nullable = false)
	private String phone_number;

	private String user_address;

	@Column(length = 8, nullable = false)
	private String user_birth;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDate user_registered_date;

	private Long user_googleId;
	private Long user_kakaoId;
	private Long user_naverId;

	@Column(length = 8, nullable = false)
	private String simple_password;
}
