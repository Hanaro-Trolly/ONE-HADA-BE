package com.example.onehada.db;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.Product;
import com.example.onehada.db.data.ButtonNode;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.repository.ButtonNodeRepository;
import com.example.onehada.db.data.repository.ProductRepository;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import com.example.onehada.db.data.service.DataTransferService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTransferServiceTest {

	@Mock
	private ProductRepository productMongoRepository;

	@Mock
	private ButtonRepository buttonMongoRepository;

	@Mock
	private ProductNodeRepository productNeo4jRepository;

	@Mock
	private ButtonNodeRepository buttonNeo4jRepository;

	@InjectMocks
	private DataTransferService dataTransferService;

	@Test
	void transferDataToNeo4j_ShouldTransferAllData() {
		// Arrange
		List<Product> products = Arrays.asList(
			new Product("product1"),
			new Product("product2")
		);

		List<Button> buttons = Arrays.asList(
			new Button("button1", "type1"),
			new Button("button2", "type2")
		);

		when(productMongoRepository.findAll()).thenReturn(products);
		when(buttonMongoRepository.findAll()).thenReturn(buttons);

		// Act
		dataTransferService.transferDataToNeo4j();

		// Assert
		verify(productNeo4jRepository, times(2)).save(any(ProductNode.class));
		verify(buttonNeo4jRepository, times(2)).save(any(ButtonNode.class));
	}

	@Test
	void transferDataToNeo4j_WithEmptyData_ShouldHandleGracefully() {
		// Arrange
		when(productMongoRepository.findAll()).thenReturn(Arrays.asList());
		when(buttonMongoRepository.findAll()).thenReturn(Arrays.asList());

		// Act
		dataTransferService.transferDataToNeo4j();

		// Assert
		verify(productNeo4jRepository, never()).save(any(ProductNode.class));
		verify(buttonNeo4jRepository, never()).save(any(ButtonNode.class));
	}
}
