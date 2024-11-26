package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class Consultation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long consultationId;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name="agent_id", nullable = false)
	private Agent agent;

	@Column(length = 100, nullable = false)
	private String consultationTitle;

	@Column(length = 1000)
	private String consultationContent;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime consultationDate;
}
