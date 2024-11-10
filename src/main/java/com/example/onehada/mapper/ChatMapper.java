package com.example.onehada.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.onehada.dto.ChatDto;

@Component
public interface ChatMapper {
	List<ChatDto> selectMessages(int num) throws Exception;
	void insertMessage(ChatDto chatDto) throws Exception;
}
