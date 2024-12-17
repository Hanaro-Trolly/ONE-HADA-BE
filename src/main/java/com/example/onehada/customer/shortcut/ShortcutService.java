package com.example.onehada.customer.shortcut;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

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

	public List<ShortcutDTO> getShortcuts(String token){
		Long userId = getUserIdFromToken(token);
		List<Shortcut> shortcuts = shortcutRepository.findShortcutByUserUserIdOrderByShortcutIdDesc(userId);
		if (shortcuts.isEmpty()) {
			throw new NotFoundException("바로가기가 존재하지 않습니다.");
		}
		return shortcuts.stream()
			.map(shortcut -> ShortcutDTO.builder()
			.shortcutId(shortcut.getShortcutId())
			.shortcutName(shortcut.getShortcutName())
			.favorite(shortcut.isFavorite())
			.build()).collect(Collectors.toList());
	}

	public ShortcutDTO createShortcut(ShortcutDTO shortcut, String token) {

		Long userId = getUserIdFromToken(token);
		User user = userRepository.findById(userId).orElseThrow( () -> new NotFoundException("유효하지 않은 사용자 입니다."));

		Shortcut newShortcut = new Shortcut();
		newShortcut.setUser(user);
		newShortcut.setShortcutName(shortcut.getShortcutName());
		newShortcut.setFavorite(false);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String elements = objectMapper.writeValueAsString(shortcut.getShortcutElements());
			newShortcut.setShortcutElements(elements);
			System.out.println("elements = " + elements);
			shortcutRepository.save(newShortcut);

		} catch (JsonProcessingException e) {
			throw new BadRequestException( "잘못된 요청입니다." + e.getMessage());
		}

		return ShortcutDTO.builder().shortcutId(newShortcut.getShortcutId()).build();
	}

	@Transactional
	public void deleteShortcut(Long shortcutId) {
		if (!shortcutRepository.existsById(shortcutId)) {
			throw new NotFoundException("존재하지 않는 바로가기입니다.");
		}
		shortcutRepository.deleteById(shortcutId);
	}

	@Transactional
	public void updateIsFavorite(Long shortcutId, String token) {
		Long userId = getUserIdFromToken(token);
		Shortcut shortcut = shortcutRepository.findByShortcutId(shortcutId);
		if (shortcut == null || !Objects.equals(shortcut.getUser().getUserId(), userId)) {
			throw new NotFoundException("존재하지 않는 바로가기 입니다.");
		}
		shortcut.setFavorite(!shortcut.isFavorite());
		shortcutRepository.save(shortcut);
	}

	@Transactional
	public List<ShortcutDTO> getFavoriteShortcuts(String token) {
		Long userId = getUserIdFromToken(token);
		List<Shortcut> favoriteShortcuts =
			shortcutRepository.findShortcutByUserUserIdAndFavoriteTrueOrderByShortcutIdDesc(userId);

		if (favoriteShortcuts.isEmpty()) {
			throw new NotFoundException("즐겨찾기 된 바로가기가 없습니다.");
		}

		return favoriteShortcuts.stream().map(shortcut -> ShortcutDTO.builder()
			.shortcutId(shortcut.getShortcutId())
			.shortcutName(shortcut.getShortcutName())
			.favorite(shortcut.isFavorite())
			.build()).collect(Collectors.toList());
	}
}
