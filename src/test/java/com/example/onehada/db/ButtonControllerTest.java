package com.example.onehada.db;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.controller.ButtonController;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ButtonControllerTest {
	@Mock
	private ButtonRepository buttonRepository;

	@InjectMocks
	private ButtonController buttonController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(buttonController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void getAllButtons_ShouldReturnListOfButtons() throws Exception {
		// Arrange
		when(buttonRepository.findAll()).thenReturn(
			Arrays.asList(
				new Button("button1", "normal"),
				new Button("button2", "start")
			)
		);

		// Act & Assert
		mockMvc.perform(get("/buttons"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].name").value("button1"))
			.andExpect(jsonPath("$[1].name").value("button2"));
	}

	@Test
	void addButton_ShouldCreateNewButton() throws Exception {
		// Arrange
		Button button = new Button("newButton", "normal");
		when(buttonRepository.save(any(Button.class))).thenReturn(button);

		// Act & Assert
		mockMvc.perform(post("/buttons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(button)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("newButton"))
			.andExpect(jsonPath("$.type").value("normal"));
	}

}
