package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long history_id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 100 , nullable = false)
	private String historyName;
	@Column(nullable = false)
	private String historyUrl;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime activityDate;
}
