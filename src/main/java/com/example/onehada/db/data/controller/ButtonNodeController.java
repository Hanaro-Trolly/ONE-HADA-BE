package com.example.onehada.db.data.controller;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.service.ButtonService;
import com.example.onehada.db.dto.ApiResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Button", description = "버튼 클릭 및 세션 관련 API")
@RequestMapping("/api/button")
public class ButtonNodeController {
    
    private final ButtonService buttonService;
    private final JwtService jwtService;

    public ButtonNodeController(ButtonService buttonService, JwtService jwtService) {
        this.buttonService = buttonService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "사용자 세션 조회", description = "특정 사용자의 버튼 클릭 세션 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음")
    })
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ButtonSession> getUserSessions(@PathVariable String userId) {
        ButtonSession sessions = buttonService.processUserClickHistory(userId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "버튼 클릭 로그 저장", description = "사용자의 버튼 클릭 이벤트를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{buttonId}")
    public ResponseEntity<ApiResult> logButtonClick(@RequestHeader("Authorization") String token,@PathVariable String buttonId) {
        String accessToken = token.replace("Bearer ", "");
        String userId = jwtService.extractUserId(accessToken).toString();
        buttonService.saveButtonLog(userId, buttonId);
        Button button = buttonService.getButtonById(buttonId);

        if ("product".equals(button.getType())) {
            buttonService.processUserClickHistory(userId);
        }

        return ResponseEntity.ok(new ApiResult(200, "OK", "버튼 클릭 로그 저장 성공", null));
    }
}
