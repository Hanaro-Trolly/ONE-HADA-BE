package com.example.onehada.db;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.db.data.ProductNode;
import com.example.onehada.db.data.controller.ProductNodeController;
import com.example.onehada.db.data.service.RecommendService;
import com.example.onehada.db.dto.ApiResult;
import com.example.onehada.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductNodeControllerTest {
	@Mock
	private RecommendService recommendService;
	@Mock
	private JwtService jwtService;

	@InjectMocks
	private ProductNodeController productNodeController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(productNodeController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void getAllProducts_ShouldReturnListOfProducts() {
		// Arrange
		List<ProductNode> expectedProducts = Arrays.asList(
			new ProductNode("product1"),
			new ProductNode("product2")
		);
		when(recommendService.findAllProducts()).thenReturn(expectedProducts);

		// Act
		List<ProductNode> result = productNodeController.getAllProducts();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("product1", result.get(0).getName());
		verify(recommendService).findAllProducts();
	}

	@Test
	void createProduct_ShouldReturnNewProduct() {
		// Arrange
		ProductNode inputProduct = new ProductNode("newProduct");
		when(recommendService.createProduct(anyString())).thenReturn(inputProduct);

		// Act
		ProductNode result = productNodeController.createProduct(inputProduct);

		// Assert
		assertNotNull(result);
		assertEquals("newProduct", result.getName());
		verify(recommendService).createProduct("newProduct");
	}

	@Test
	void addRecommend_ShouldReturnSuccessMessage() {
		// Arrange
		String buttonName = "button1";
		String productName = "product1";

		// Act
		String result = productNodeController.addRecommend(buttonName, productName);

		// Assert
		assertEquals("button1 recommend product1", result);
		verify(recommendService).addRecommend(buttonName, productName);
	}

	@Test
	void getRecommend_ShouldReturnProductSet() {
		// Arrange
		String productName = "product1";
		Set<ProductNode> expectedProducts = new HashSet<>(Arrays.asList(
			new ProductNode("recommended1"),
			new ProductNode("recommended2")
		));
		when(recommendService.findRecommends(productName)).thenReturn(expectedProducts);

		// Act
		Set<ProductNode> result = productNodeController.getRecommend(productName);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(recommendService).findRecommends(productName);
	}
}
