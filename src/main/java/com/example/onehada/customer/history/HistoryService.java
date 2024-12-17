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
		List<History> histories = historyRepository.findHistoryByUserUserIdOrderByHistoryIdDesc(userId);

		return histories.stream()
			.map(history -> {
				Map<String, Object> historyElements = getHistoryElements(history);

				return new HistoryDTO(
					history.getHistoryId(),
					history.getUser().getUserId(),
					history.getHistoryName(),
					historyElements,
					history.getActivityDate()
				);
			}).collect(Collectors.toList());
	}

	public HistoryDTO getUserHistory(Long historyId, String token) {
		Long userId = getUserIdFromToken(token);
		History history = historyRepository.findHistoryByHistoryIdAndUserUserId(historyId, userId);
		if (history == null) {
			throw new NotFoundException("해당 활동 내역을 찾을 수 없습니다.");
		}
		Map<String, Object> historyElements = getHistoryElements(history);

		return HistoryDTO.builder()
			.historyId(history.getHistoryId())
			.userId(history.getUser().getUserId())
			.historyName(history.getHistoryName())
			.historyElements(historyElements)
			.build();
	}

	private static Map<String, Object> getHistoryElements(History history) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> historyElements;
		try {
			historyElements = objectMapper.readValue(
				history.getHistoryElements(),
				new TypeReference<>() {
				}
			);
		} catch (Exception e) {
			throw new RuntimeException("JSON 파싱 에러: " + e.getMessage(), e);
		}
		return historyElements;
	}

	public HistoryDTO createHistory(HistoryDTO history, String token) {
		Long userId = getUserIdFromToken(token);
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유효하지 않은 사용자 입니다."));

		History newHistory = new History();
		newHistory.setUser(user);
		newHistory.setHistoryName(history.getHistoryName());
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
