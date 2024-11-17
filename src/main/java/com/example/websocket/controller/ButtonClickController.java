package com.example.websocket.controller;

import com.example.websocket.model.ButtonClickEvent;
import com.example.websocket.service.ButtonLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ButtonClickController {

	private final ButtonLogService buttonLogService;

	@MessageMapping("/button.click")
	@SendTo("/topic/consultant/button-logs")
	public ButtonClickEvent handleButtonClick(ButtonClickEvent event) {
		return buttonLogService.processButtonClick(event);
	}
}
