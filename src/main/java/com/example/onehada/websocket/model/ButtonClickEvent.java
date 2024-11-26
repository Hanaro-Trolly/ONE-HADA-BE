package com.example.onehada.websocket.model;

import lombok.Data;

@Data
public class ButtonClickEvent {
	private String type;
	private String customerId;
	private String buttonId;
	private String timestamp;
}
