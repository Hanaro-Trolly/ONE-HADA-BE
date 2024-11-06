package com.example.onehada.db.entity;

import jakarta.persistence.*;

@Entity
public class User {
	@Id
	private Long id;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
