package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class Agent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int agent_id;

	@Column(length = 127, nullable = false)
	private String agent_name;

	@Column(length = 127, nullable = false)
	private String agent_email;

	private String agent_pw;

}
