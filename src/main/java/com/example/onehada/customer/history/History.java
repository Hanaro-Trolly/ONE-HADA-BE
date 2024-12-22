package com.example.onehada.customer.history;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.onehada.customer.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 100 , nullable = false)
	private String historyName;

	@Column(name = "history_elements", columnDefinition = "JSON")
	private String historyElements;

	@Column(name = "history_url")
	private String historyUrl;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime activityDate;
}
