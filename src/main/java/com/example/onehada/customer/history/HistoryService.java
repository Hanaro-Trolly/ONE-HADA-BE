package com.example.onehada.customer.history;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.exception.NotFoundException;

@Service
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final JwtService jwtService;

	public HistoryService(HistoryRepository historyRepository, JwtService jwtService) {
		this.historyRepository = historyRepository;
		this.jwtService = jwtService;
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
		return histories.stream().map(history-> new HistoryDTO(history.getHistoryId(),
			history.getUser().getUserId(),
			history.getHistoryName(),
			history.getHistoryUrl(),
			history.getActivityDate())).collect(Collectors.toList());
	}
}
