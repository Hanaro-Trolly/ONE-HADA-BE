package com.example.onehada.customer.consultation;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.onehada.customer.agent.Agent;
import com.example.onehada.customer.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long consultationId;

	@ManyToOne
	@JoinColumn(name="user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
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
