package com.example.onehada.db;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonLog;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.service.ButtonService;
import com.example.onehada.db.data.service.RecommendService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ButtonServiceTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private RecommendService recommendService;

	@Mock
	private ButtonRepository buttonRepository;

	@InjectMocks
	private ButtonService buttonService;

	@Test
	void saveButtonLog_ShouldCreateAndSaveButtonLog() {
		// Arrange
		String userId = "testUser";
		String buttonId = "testButton";
		Button button = new Button(buttonId, "normal");
		LocalDateTime now = LocalDateTime.now();

		ButtonLog expectedLog = ButtonLog.builder()
			.userId(userId)
			.buttonId(buttonId)
			.buttonName(button.getName())
			.buttonType(button.getType())
			.clickTime(now)
			.build();

		when(buttonRepository.findByName(buttonId)).thenReturn(button);
		when(mongoTemplate.save(any(ButtonLog.class))).thenReturn(expectedLog);

		// Act
		buttonService.saveButtonLog(userId, buttonId);

		// Assert
		verify(mongoTemplate).save(any(ButtonLog.class));
		verify(buttonRepository).findByName(buttonId);
	}

	@Test
	void processUserClickHistory_ShouldReturnButtonSession() {
		// Arrange
		String userId = "testUser";
		LocalDateTime now = LocalDateTime.now();

		ButtonLog productClick = ButtonLog.builder()
			.id("2")
			.userId(userId)
			.buttonId("productButton")
			.buttonType("product")
			.buttonName("Product Button")
			.clickTime(now)
			.build();

		ButtonLog startClick = ButtonLog.builder()
			.id("1")
			.userId(userId)
			.buttonId("startButton")
			.buttonType("start")
			.buttonName("Start Button")
			.clickTime(now.minusMinutes(5))
			.build();

		when(mongoTemplate.findOne(any(Query.class), eq(ButtonLog.class)))
			.thenReturn(productClick)
			.thenReturn(startClick);

		// Act
		ButtonSession result = buttonService.processUserClickHistory(userId);

		// Assert
		assertNotNull(result);
		assertEquals(startClick, result.getFirstButton());
		assertEquals(productClick, result.getLastButton());
		verify(recommendService).addRecommend(startClick.getButtonName(), productClick.getButtonName());
	}

	@Test
	void getButtonByName_ShouldReturnButton() {
		// Arrange
		String buttonName = "testButton";
		Button expectedButton = new Button(buttonName, "normal");
		when(buttonRepository.findByName(buttonName)).thenReturn(expectedButton);

		// Act
		Button result = buttonService.getButtonByName(buttonName);

		// Assert
		assertNotNull(result);
		assertEquals(buttonName, result.getName());
		assertEquals("normal", result.getType());
		verify(buttonRepository).findByName(buttonName);
	}

	@Test
	void saveButton_ShouldSaveAndReturnButton() {
		// Arrange
		Button button = new Button("newButton", "normal");
		when(buttonRepository.save(any(Button.class))).thenReturn(button);

		// Act
		Button result = buttonService.saveButton(button);

		// Assert
		assertNotNull(result);
		assertEquals("newButton", result.getName());
		assertEquals("normal", result.getType());
		verify(buttonRepository).save(button);
	}

	@Test
	void processUserClickHistory_WithNoProductClick_ShouldReturnNull() {
		// Arrange
		String userId = "testUser";
		when(mongoTemplate.findOne(any(Query.class), eq(ButtonLog.class))).thenReturn(null);

		// Act
		ButtonSession result = buttonService.processUserClickHistory(userId);

		// Assert
		assertNull(result);
		verify(recommendService, never()).addRecommend(any(), any());
	}

	@Test
	void processUserClickHistory_WithNoStartClick_ShouldReturnNull() {
		// Arrange
		String userId = "testUser";
		ButtonLog productClick = ButtonLog.builder()
			.userId(userId)
			.buttonId("productButton")
			.buttonType("product")
			.clickTime(LocalDateTime.now())
			.build();

		when(mongoTemplate.findOne(any(Query.class), eq(ButtonLog.class)))
			.thenReturn(productClick)
			.thenReturn(null);

		// Act
		ButtonSession result = buttonService.processUserClickHistory(userId);

		// Assert
		assertNull(result);
		verify(recommendService, never()).addRecommend(any(), any());
	}
}
