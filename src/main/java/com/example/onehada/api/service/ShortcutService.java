package com.example.onehada.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.ShortcutDTO;
import com.example.onehada.db.entity.Shortcut;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.ShortcutRepository;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class ShortcutService {
	private final ShortcutRepository shortcutRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public ShortcutService(ShortcutRepository shortcutRepository, UserRepository userRepository, JwtService jwtService) {
		this.shortcutRepository = shortcutRepository;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	private Long getUserIdFromToken(String token) {
		String accessToken = token.replace("Bearer ", "");
		return jwtService.extractUserId(accessToken);
	}

	public List<Shortcut> getShortcut(String token){
		Long userId = getUserIdFromToken(token);
		List<Shortcut> shortcuts = shortcutRepository.findShortByUserUserId(userId);
		if (shortcuts.isEmpty()) {
			throw new NotFoundException("바로가기가 존재하지 않습니다.");
		}
		return shortcuts;
	}

	public ShortcutDTO createShortcut(ShortcutDTO shortcut, String token) {
		Long userId = getUserIdFromToken(token);
		User user = userRepository.findById(userId).orElseThrow( () -> new NotFoundException("유효하지 않은 사용자 입니다."));

		Shortcut newShortcut = new Shortcut();
		ObjectMapper objectMapper = new ObjectMapper();


		System.out.println("newShortcut = " + newShortcut);
		System.out.println("newShortcut elements= " + shortcut.getHistory_elements());
		newShortcut.setUser(user);
		newShortcut.setShortcutName(shortcut.getShortcut_name());
		System.out.println("newsourtcut element = " + shortcut.getHistory_elements());
		System.out.println("newShortcut name= " + newShortcut.getShortcutName());
		System.out.println("newShortcut elements = " + shortcut.getHistory_elements());
		newShortcut.setShortcutUrl("/");
		newShortcut.setFavorite(false);
		shortcutRepository.save(newShortcut);

		return ShortcutDTO.builder().shortcut_id(newShortcut.getShortcutId()).build();
	}

	@Transactional
	public void deleteShortcut(Long shortcutId) {
		if (!shortcutRepository.existsById(shortcutId)) {
			throw new NotFoundException("존재하지 않는 바로가기입니다.");
		}
		shortcutRepository.deleteById(shortcutId);
	}
}
