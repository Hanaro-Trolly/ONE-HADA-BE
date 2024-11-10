package com.example.onehada.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/*
 * WebSocket 클라이언트에서 간단한 메시징 프로토콜(예: STOMP)로 메시지 처리를 구성하는 방법을 정의
 * 웹 소켓 서버를 활성화(#1)하고,
 * WebSocketMessageBrokerConfigurer 인터페이스에서 웹 소켓 연결을 구성하는 메서드(#2, #3)를 구현
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	// #2
	// 특정 URL에 매핑하는 STOMP 엔드포인트를 등록하고 (선택 사항) SockJS 폴백 옵션을 활성화 및 구성
	// addEndpoint              주어진 매핑 경로에서 WebSocket 끝점을 통해 STOMP를 등록
	// setAllowedOriginPatterns 브라우저에서 교차 출처 요청이 허용되는 출처를 패턴으로 설정
	// withSockJS               SockJS 폴백 옵션을 활성화
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
	}

	// #3
	// 메시지 브로커 옵션을 구성
	// #3-1 /app로 시작하는 메시지를 메시지 처리 메서드로 라우팅
	// #3-2 /topic으로 시작하는 메시지를 메시지 브로커로 라우팅
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app"); // #3-1
		registry.enableSimpleBroker("/topic"); // #3-2
	}
}
