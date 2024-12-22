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

import com.example.onehada.db.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Shortcut", description = "바로가기 관련 API")
@RequestMapping("/api/shortcut")
public class ShortcutController {

	private final ShortcutService shortcutService;

	public ShortcutController(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
	}

	@Operation(summary = "바로가기 목록 조회", description = "사용자의 모든 바로가기 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("")
	public ResponseEntity<ApiResult> getShortcuts(@RequestHeader("Authorization") String token) {
		List<ShortcutDTO> shortcuts = shortcutService.getShortcuts(token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "바로가기 목록 조회 성공", Map.of("shortcuts", shortcuts)));
	}

	@Operation(summary = "바로가기 생성", description = "새로운 바로가기를 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("")
	public ResponseEntity<?> addShortcut(@RequestHeader("Authorization") String token,
		@RequestBody ShortcutDTO shortcut) {
		ShortcutDTO shortcutDTO = shortcutService.createShortcut(shortcut, token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "바로가기 등록 성공", Map.of("shortcutId",
			shortcutDTO.getShortcutId())));
	}

	@Operation(summary = "바로가기 삭제", description = "특정 바로가기를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "404", description = "바로가기를 찾을 수 없음")
	})
	@DeleteMapping("/{shortcutId}")
	public ResponseEntity<?> deleteShortcut(@PathVariable Long shortcutId) {
		shortcutService.deleteShortcut(shortcutId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "바로가기가 삭제되었습니다.", null));
	}

	@Operation(summary = "즐겨찾기 상태 변경", description = "바로가기의 즐겨찾기 상태를 토글합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "변경 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "바로가기를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@PatchMapping("/{shortcutId}/favorite")
	public ResponseEntity<?> updateIsFavorite(@PathVariable Long shortcutId,
		@RequestHeader("Authorization") String token) {
		shortcutService.updateIsFavorite(shortcutId, token);
		return ResponseEntity.ok(new ApiResult(200, "OK", "즐겨찾기 설정이 변경되었습니다.", null));
	}

	@Operation(summary = "즐겨찾기 목록 조회", description = "즐겨찾기로 설정된 바로가기 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/favorite")
	public ResponseEntity<?> getFavoriteShortcuts(@RequestHeader("Authorization") String token) {
		List<ShortcutDTO> shortcuts = shortcutService.getFavoriteShortcuts(token);

		return ResponseEntity.ok(new ApiResult(200, "OK", "즐겨찾기 목록 조회 성공", Map.of("shortcuts", shortcuts)));
	}
}
