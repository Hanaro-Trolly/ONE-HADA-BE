package com.example.onehada.customer.shortcut;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/shortcut")
public class ShortcutController {

	private final ShortcutService shortcutService;

	public ShortcutController(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse> getShortcuts(@RequestHeader("Authorization") String token) {
		List<ShortcutDTO> shortcuts = shortcutService.getShortcut(token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 목록 조회 성공", Map.of("shortcuts", shortcuts)));
	}

	@PostMapping("")
	public ResponseEntity<?> addShortcut(@RequestHeader("Authorization") String token,
		@RequestBody ShortcutDTO shortcut) {
		ShortcutDTO shortcutDTO = shortcutService.createShortcut(shortcut, token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 등록 성공", Map.of("shortcut",
			shortcutDTO.getShortcutId())));
	}


	@DeleteMapping("/{shortcutId}")
	public ResponseEntity<?> deleteShortcut(@PathVariable Long shortcutId) {
		shortcutService.deleteShortcut(shortcutId);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "바로가기 삭제 성공", null));
	}

	@PatchMapping("/{shortcutId}/favorite")
	public ResponseEntity<?> updateIsFavorite(@PathVariable Long shortcutId,
		@RequestHeader("Authorization") String token) {
		shortcutService.updateIsFavorite(shortcutId, token);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "즐겨찾기 설정이 변경되었습니다.", null));
	}

	@GetMapping("/favorite")
	public ResponseEntity<?> getFavoriteShortcuts(@RequestHeader("Authorization") String token) {
		List<ShortcutDTO> shortcuts = shortcutService.getFavoriteShortcuts(token);

		return ResponseEntity.ok(new ApiResponse(200, "OK", "즐겨찾기 목록 조회 성공", Map.of("shortcuts", shortcuts)));
	}
}
