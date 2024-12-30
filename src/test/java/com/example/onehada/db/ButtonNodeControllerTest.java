package com.example.onehada.db;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.controller.ButtonNodeController;
import com.example.onehada.db.data.service.ButtonService;
import com.example.onehada.db.data.service.RecommendService;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.exception.GlobalExceptionHandler;
import com.example.onehada.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ButtonNodeControllerTest {
	@Mock
	private ButtonService buttonService;
	@Mock
	private JwtService jwtService;
	@Mock
	private RecommendService recommendService;

	@InjectMocks
	private ButtonNodeController buttonNodeController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(buttonNodeController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void getUserSessions_ShouldReturnButtonSession() {
		// Arrange
		String userId = "1";
		ButtonSession expectedSession = new ButtonSession(null, null);
		when(buttonService.processUserClickHistory(userId)).thenReturn(expectedSession);

		// Act
		ResponseEntity<ButtonSession> response = buttonNodeController.getUserSessions(userId);

		// Assert
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(expectedSession, response.getBody());
	}


	@Test
	void logButtonClick_WithInvalidToken_ShouldThrowUnauthorizedException() {
		// Arrange
		String token = "Bearer invalidToken";
		String buttonId = "testButton";

		when(jwtService.extractUserId(any())).thenThrow(new UnauthorizedException("Invalid token"));

		// Act & Assert
		assertThrows(UnauthorizedException.class, () ->
			buttonNodeController.logButtonClick(token, buttonId));
	}
}
