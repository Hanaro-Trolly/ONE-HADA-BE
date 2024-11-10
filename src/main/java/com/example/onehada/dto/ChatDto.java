package com.example.onehada.dto;

import java.util.List;

import com.example.onehada.db.entity.Chat;

import lombok.Data;

@Data
public class ChatDto {
	private MessageType type;
	private String message;
	private String sender;
	private String createdDt;
	private List<ChatDto> history;

	public void setType(Chat.MessageType type) {
	}

	public enum MessageType {
		JOIN, CHAT, LEAVE
	}
}
