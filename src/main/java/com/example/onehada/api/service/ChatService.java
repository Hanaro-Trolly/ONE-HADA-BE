package com.example.onehada.api.service;

import java.util.List;

import com.example.onehada.dto.ChatDto;

public interface ChatService {
	List<ChatDto> selectMessages() throws Exception;
	void insertMessage(ChatDto chatDto) throws Exception;
}
