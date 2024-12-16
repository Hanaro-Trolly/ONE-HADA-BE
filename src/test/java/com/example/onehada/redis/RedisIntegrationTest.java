package com.example.onehada.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class RedisIntegrationTest {

	@Container
	public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6-alpine"))
		.withExposedPorts(6379);

	@Autowired
	private RedisService redisService;

	@Test
	public void testTokenOperations() {
		// Given
		String email = "test@test.com";
		String refreshToken = "test-refresh-token";
		long expiration = 3600000; // 1시간

		// When
		redisService.saveRefreshToken(email, refreshToken, expiration);

		// Then
		assertEquals(refreshToken, redisService.getRefreshToken(email));
	}

	@Test
	public void testBlacklistOperations() {
		// Given
		String token = "test-token";
		long expiration = 3600000;

		// When
		redisService.addToBlacklist(token, expiration);

		// Then
		assertTrue(redisService.isBlacklisted(token));
	}

	@Test
	public void testRefreshTokenDeletion() {
		// Given
		String email = "test@test.com";
		String refreshToken = "test-refresh-token";
		long expiration = 3600000;

		// When
		redisService.saveRefreshToken(email, refreshToken, expiration);
		redisService.deleteRefreshToken(email);

		// Then
		assertNull(redisService.getRefreshToken(email));
	}
}
