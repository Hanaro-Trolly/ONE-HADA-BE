package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Shortcut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long shortcutId;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@Column(length = 100, nullable = false)
	private String shortcutName;

	@Lob
	@Column(name = "shorcut_elements", columnDefinition = "JSON")
	private String shortcutElements;

	@Column(nullable = false)
	private boolean isFavorite = false;
}
