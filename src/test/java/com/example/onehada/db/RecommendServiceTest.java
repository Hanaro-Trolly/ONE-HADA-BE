package com.example.onehada.db;

import com.example.onehada.db.data.ButtonIdDTO;
import com.example.onehada.db.data.ButtonNode;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.repository.ButtonNodeRepository;
import com.example.onehada.db.data.repository.ButtonRepository;
import com.example.onehada.db.data.repository.ProductNodeRepository;
import com.example.onehada.db.data.service.RecommendService;
import com.example.onehada.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

	@Mock
	private ButtonNodeRepository buttonNodeRepository;

	@Mock
	private ProductNodeRepository productNodeRepository;

	@Mock
	private ButtonRepository buttonRepository;

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private AggregationResults<ButtonIdDTO> aggregationResults;

	@InjectMocks
	private RecommendService recommendService;



	@Test
	void addRecommend_ShouldCreateRelationship() {
		// Arrange
		String buttonName = "testButton";
		String productName = "testProduct";

		ButtonNode buttonNode = new ButtonNode(buttonName);
		buttonNode.setRecommendedProducts(new HashSet<>());  // Initialize the Set

		ProductNode productNode = new ProductNode(productName);

		when(buttonNodeRepository.findById(buttonName)).thenReturn(Optional.of(buttonNode));
		when(productNodeRepository.findById(productName)).thenReturn(Optional.of(productNode));

		// Act & Assert
		assertDoesNotThrow(() -> recommendService.addRecommend(buttonName, productName));
		verify(buttonNodeRepository).incrementRecommendationWeight(
			eq(buttonName),
			eq(productName),
			any(LocalDateTime.class)
		);
	}


	@Test
	void createProduct_ShouldReturnNewProduct() {
		// Arrange
		String productName = "newProduct";
		ProductNode expectedProduct = new ProductNode(productName);
		when(productNodeRepository.save(any(ProductNode.class))).thenReturn(expectedProduct);

		// Act
		ProductNode result = recommendService.createProduct(productName);

		// Assert
		assertNotNull(result);
		assertEquals(productName, result.getName());
		verify(productNodeRepository).save(any(ProductNode.class));
	}

	@Test
	void findAllProducts_ShouldReturnAllProducts() {
		// Arrange
		List<ProductNode> expectedProducts = Arrays.asList(
			new ProductNode("product1"),
			new ProductNode("product2")
		);
		when(productNodeRepository.findAll()).thenReturn(expectedProducts);

		// Act
		List<ProductNode> result = recommendService.findAllProducts();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(productNodeRepository).findAll();
	}
}
