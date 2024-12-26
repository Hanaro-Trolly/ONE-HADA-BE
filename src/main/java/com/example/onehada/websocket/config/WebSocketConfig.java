package com.example.onehada.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}


	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setAllowedOrigins("http://localhost:3000", "https://onehada.site")
			.withSockJS()
			.setStreamBytesLimit(1024 * 1024 * 64);  // Stream 크기 설정 (512 KB)
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		// WebSocket 메시지 크기 설정
		registration.setMessageSizeLimit(1024 * 1024 * 64);
		// 전송 버퍼 크기 설정 (1024 KB)
		registration.setSendBufferSizeLimit(1024 * 1024);
	}
}
