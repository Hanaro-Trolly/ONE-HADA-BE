package com.example.onehada.db.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long historyId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 100 , nullable = false)
	private String historyName;

	@Lob
	@Column(name = "history_elements", columnDefinition = "JSON")
	private String historyElements;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime activityDate;
}
