package com.example.onehada.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	@Column(name = "shortcut_elements", columnDefinition = "JSON")
	private String shortcutElements;

	@Column (name = "shortcut_url")
	private String shortcutUrl;

	@Column(nullable = false)
	private boolean isFavorite = false;
}
