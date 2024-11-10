package com.example.onehada.api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onehada.db.entity.Chat;
import com.example.onehada.db.repository.ChatRepository;
import com.example.onehada.dto.ChatDto;
import com.example.onehada.mapper.ChatMapper;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatRepository chatRepository;

	// 한번에 가져올 이전 대화의 개수
	private final int CONST_MAX_MESSAGE_COUNT = 10;

	@Override
	public List<ChatDto> selectMessages() throws Exception {
		// Chat 엔티티 목록을 조회하고 ChatDto로 변환하여 반환
		List<Chat> chats = chatRepository.findTopByOrderByCreatedDtDesc(CONST_MAX_MESSAGE_COUNT);
		return chats.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}
	@Override
	public void insertMessage(ChatDto chatDto) throws Exception {
		// ChatDto를 Chat 엔티티로 변환하여 저장
		Chat chat = convertToEntity(chatDto);
		chat.setCreatedDt(LocalDateTime.now());
		chatRepository.save(chat);
	}
	// Chat 엔티티를 ChatDto로 변환
	private ChatDto convertToDto(Chat chat) {
		ChatDto dto = new ChatDto();
		dto.setType(chat.getType());
		dto.setMessage(chat.getMessage());
		dto.setSender(chat.getSender());
		dto.setCreatedDt(chat.getCreatedDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		return dto;
	}
	// ChatDto를 Chat 엔티티로 변환
	private Chat convertToEntity(ChatDto chatDto) {
		Chat chat = new Chat();
		chat.setType(chatDto.getType());
		chat.setMessage(chatDto.getMessage());
		chat.setSender(chatDto.getSender());
		return chat;
	}
}
