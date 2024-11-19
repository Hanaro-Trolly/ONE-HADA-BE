package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Shortcut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long shortcut_id;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@Column(length = 100, nullable = false)
	private String shortcut_name;

	@Column(nullable = false)
	private String shortcut_url;

	@Column(nullable = false)
	private boolean is_favorite = false;
}
