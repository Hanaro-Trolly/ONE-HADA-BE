package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Agent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int agentId;

	@Column(length = 127, nullable = false)
	private String agentName;

	@Column(length = 127, nullable = false)
	private String agentEmail;

	private String agentPw;

}
