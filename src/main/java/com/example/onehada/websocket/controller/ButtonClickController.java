package com.example.onehada.websocket.controller;

import com.example.onehada.websocket.model.ButtonClickEvent;
import com.example.onehada.websocket.model.ConsultationEndEvent;
import com.example.onehada.websocket.service.ButtonLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ButtonClickController {

	private final ButtonLogService buttonLogService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/button.click")
	@SendTo("/topic/consultant/button-logs")
	public ButtonClickEvent handleButtonClick(ButtonClickEvent event) {
		return buttonLogService.processButtonClick(event);
	}

	@MessageMapping("/topic/customer/{userId}/end-consultation")
	public void handleConsultationEnd(ConsultationEndEvent event, @DestinationVariable String userId) {
		messagingTemplate.convertAndSend(
			"/topic/customer/" + userId + "/end-consultation",
			event
		);
	}

}
