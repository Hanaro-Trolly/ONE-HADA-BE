package com.example.onehada.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.ShortcutService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.ShortcutDTO;
import com.example.onehada.db.entity.Shortcut;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shortcut")
public class ShortcutController {

	private final ShortcutService shortcutService;

	public ShortcutController(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse> getShortcuts(@RequestHeader("Authorization") String token) {
		List<Shortcut> shortcuts = shortcutService.getShortcut(token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 목록 조회 성공", Map.of("shortcuts", shortcuts)));
	}

	@PostMapping("")
	public ResponseEntity<?> addShortcut(@RequestHeader("Authorization") String token,
		@RequestBody ShortcutDTO shortcut) {
		ShortcutDTO shortcutDTO = shortcutService.createShortcut(shortcut, token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 등록 성공", Map.of("shortcut",
			shortcutDTO.getShortcut_id())));
	}


	@DeleteMapping("/{shortcut_id}")
	public ResponseEntity<?> deleteShortcut(@PathVariable Long shortcut_id) {
		shortcutService.deleteShortcut(shortcut_id);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 삭제 성공", null));
	}

}
