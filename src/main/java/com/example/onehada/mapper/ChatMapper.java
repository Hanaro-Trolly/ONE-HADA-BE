package com.example.onehada.mapper;

import java.util.List;

import com.example.onehada.dto.ChatDto;

public interface ChatMapper {
	List<ChatDto> selectMessages(int num) throws Exception;
	void insertMessage(ChatDto chatDto) throws Exception;
}
