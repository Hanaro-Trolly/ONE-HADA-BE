package com.example.onehada.db.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.example.onehada.dto.ChatDto;

@Entity
@Data
@Table(name = "t_chat")
public class Chat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MessageType type = MessageType.CHAT;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private String sender;

	@Column(name = "created_dt", nullable = false)
	private LocalDateTime createdDt;

	public void setType(ChatDto.MessageType type) {
	}

	public void setType(MessageType messageType) {
	}

	public enum MessageType {
		JOIN, CHAT, LEAVE
	}
}
