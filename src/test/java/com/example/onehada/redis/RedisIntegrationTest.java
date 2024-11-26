package com.example.onehada.redis;

import com.example.onehada.api.service.RedisService;
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
		String accessToken = "test-access-token";
		String refreshToken = "test-refresh-token";
		long expiration = 3600000; // 1시간

		// When
		redisService.saveAccessToken(email, accessToken, expiration);
		redisService.saveRefreshToken(email, refreshToken, expiration);

		// Then
		assertEquals(accessToken, redisService.getAccessToken(email));
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
	public void testTokenDeletion() {
		// Given
		String email = "test@test.com";
		String accessToken = "test-access-token";
		long expiration = 3600000;

		// When
		redisService.saveAccessToken(email, accessToken, expiration);
		redisService.deleteValue("access:" + email);

		// Then
		assertNull(redisService.getAccessToken(email));
	}

/*
	@Test
	public void testActiveTokenCount() {
		// Given
		String email = "test@test.com";
		String accessToken = "test-access-token";
		long expiration = 3600000;

		// When
		redisService.saveActiveToken(email, accessToken, expiration);

		// Then
		Long activeTokens = redisService.getActiveTokenCount(email);
		System.out.println("activeTokens = " + activeTokens);
		assertTrue(activeTokens >= 1);
	}
*/
}
