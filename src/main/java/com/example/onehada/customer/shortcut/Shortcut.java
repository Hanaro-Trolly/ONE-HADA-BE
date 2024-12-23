package com.example.onehada.customer.shortcut;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.onehada.customer.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shortcut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shortcutId;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Column(length = 100, nullable = false)
	private String shortcutName;

	@Column(name = "shortcut_elements", columnDefinition = "JSON")
	private String shortcutElements;

	@Column(name = "is_favorite", nullable = false)
	@Builder.Default
	private boolean favorite = false;
}
