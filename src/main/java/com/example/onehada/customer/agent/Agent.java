package com.example.onehada.customer.agent;

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
	@Column(name = "agent_id")
	private Long agentId;

	@Column(length = 127, nullable = false)
	private String agentName;

	@Column(length = 127, nullable = false)
	private String agentEmail;

	private String agentPw;

}
