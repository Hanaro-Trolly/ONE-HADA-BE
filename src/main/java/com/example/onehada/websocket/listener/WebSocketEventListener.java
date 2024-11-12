package com.example.onehada.websocket.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import com.example.onehada.db.entity.Chat;
import com.example.onehada.db.repository.ChatRepository;
import com.example.onehada.dto.ChatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class WebSocketEventListener {

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Autowired
	private ChatRepository chatRepository;

	// 웹 소켓 연결 이벤트
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		log.info("Received a new web socket connection: [event detail -> "+ event + "]");
	}

	// 웹 소켓 연결 해제 이벤트
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username != null) {
			log.info("User Disconnected : " + username);

			// Chat 엔티티 생성 및 설정
			Chat chatMessage = new Chat();
			chatMessage.setType(Chat.MessageType.LEAVE);
			chatMessage.setSender(username);
			chatMessage.setMessage(username + "님이 퇴장하셨습니다.");
			chatMessage.setCreatedDt(LocalDateTime.now());

			// DB에 퇴장 메시지 저장
			chatRepository.save(chatMessage);

			// Chat 엔티티를 ChatDto로 변환 후 메시지 전송
			ChatDto chatDto = convertToDto(chatMessage);
			messagingTemplate.convertAndSend("/topic/chatting", chatDto);
		}
	}

	// Chat 엔티티를 ChatDto로 변환하는 메서드
	private ChatDto convertToDto(Chat chat) {
		ChatDto dto = new ChatDto();
		dto.setType(chat.getType());
		dto.setSender(chat.getSender());
		dto.setMessage(chat.getMessage());
		dto.setCreatedDt(chat.getCreatedDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		return dto;
	}
}
