package com.example.onehada.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int agentId;

	@Column(length = 127, nullable = false)
	private String agentName;

	@Column(length = 127, nullable = false)
	private String agentEmail;

	@Column(nullable = false)
	private String agentPw;
}
