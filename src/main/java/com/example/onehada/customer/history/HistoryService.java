package com.example.onehada.customer.history;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public HistoryService(HistoryRepository historyRepository, JwtService jwtService, UserRepository userRepository) {
		this.historyRepository = historyRepository;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	private Long getUserIdFromToken(String token) {
		String accessToken = token.replace("Bearer ", "");
		return jwtService.extractUserId(accessToken);
	}

	public List<HistoryDTO> getUserHistories(String token) {
		Long userId = getUserIdFromToken(token);
		List<History> histories = historyRepository.findHistoryByUserUserId(userId);
		if (histories.isEmpty()) {
			throw new NotFoundException("활동 내역이 존재하지 않습니다.");
		}

		ObjectMapper objectMapper = new ObjectMapper();

		return histories.stream()
			.map(history -> {
				Map<String, Object> historyElements;
				try {
					historyElements = objectMapper.readValue(
						history.getHistoryElements(),
						new TypeReference<>() {}
					);
				} catch (Exception e) {
					throw new RuntimeException("JSON 파싱 에러: " + e.getMessage(), e);
				}

				return new HistoryDTO(
					history.getHistoryId(),
					history.getUser().getUserId(),
					history.getHistoryName(),
					history.getHistoryUrl(),
					historyElements,
					history.getActivityDate()
				);
			}).collect(Collectors.toList());
	}

	public HistoryDTO createHistory(HistoryDTO history, String token) {
		Long userId = getUserIdFromToken(token);
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유효하지 않은 사용자 입니다."));

		History newHistory = new History();
		newHistory.setUser(user);
		newHistory.setHistoryName(history.getHistoryName());
		newHistory.setHistoryUrl(history.getHistoryUrl());
		newHistory.setActivityDate(history.getActivityDate());

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String elements = objectMapper.writeValueAsString(history.getHistoryElements());
			newHistory.setHistoryElements(elements);
			historyRepository.save(newHistory);
		} catch (JsonProcessingException e) {
			throw new BadRequestException("잘못된 요청입니다." + e.getMessage());
		}
		return HistoryDTO.builder().historyId(newHistory.getHistoryId()).build();
	}
}
