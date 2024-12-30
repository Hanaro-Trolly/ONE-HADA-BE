package com.example.onehada.db;

import com.example.onehada.db.data.controller.DataTransferController;
import com.example.onehada.db.data.service.DataTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class DataTransferControllerTest {
	@Mock
	private DataTransferService dataTransferService;

	private MockMvc mockMvc;
	private DataTransferController dataTransferController;

	@BeforeEach
	void setUp() {
		dataTransferController = new DataTransferController(dataTransferService);
		mockMvc = MockMvcBuilders.standaloneSetup(dataTransferController).build();
	}

	@Test
	void transferData_ShouldReturnSuccessMessage() throws Exception {
		doNothing().when(dataTransferService).transferDataToNeo4j();

		mockMvc.perform(post("/api/transfer"))
			.andExpect(status().isOk())
			.andExpect(content().string("Data transfer from MongoDB to Neo4j completed successfully!"));
	}

	@Test
	void transferData_DirectCall_ShouldReturnSuccessMessage() {
		doNothing().when(dataTransferService).transferDataToNeo4j();

		String result = dataTransferController.transferData();

		assertEquals("Data transfer from MongoDB to Neo4j completed successfully!", result);
	}
}
