package com.example.onehada.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long agentId;

	@Column(length = 127, nullable = false)
	private String agentName;

	@Column(length = 127, nullable = false)
	private String agentEmail;

	private String agentPw;

}
