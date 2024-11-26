package com.example.onehada.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@PostConstruct
	public void testConnection() {
		try {
			redisTemplate.opsForValue().set("test", "Hello Redis!");
			String value = (String) redisTemplate.opsForValue().get("test");
			System.out.println("Redis Test Value: " + value);
		} catch (Exception e) {
			System.err.println("Redis Connection Failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 기존 메소드들 유지
	public void saveValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public String getValue(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	public void deleteValue(String key) {
		redisTemplate.delete(key);
	}

	// 새로운 메소드들 추가
	public void saveAccessToken(String email, String token, long expiration) {
		redisTemplate.opsForValue().set(
			"access:" + email,
			token,
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public void saveRefreshToken(String email, String token, long expiration) {
		redisTemplate.opsForValue().set(
			"refresh:" + email,
			token,
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public void saveActiveToken(String email, String token, long expiration) {
		redisTemplate.opsForValue().set(
			"active:" + email,
			token,
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public void addToBlacklist(String token, long expiration) {
		redisTemplate.opsForValue().set(
			"blacklist:" + token,
			"true",
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(
			redisTemplate.hasKey("blacklist:" + token)
		);
	}

	public Long getActiveTokenCount(String email) {
		return (long)redisTemplate.keys("access:" + email + ":*").size();
	}

	public String getAccessToken(String email) {
		return (String) redisTemplate.opsForValue().get("access:" + email);
	}

	public String getRefreshToken(String email) {
		return (String) redisTemplate.opsForValue().get("refresh:" + email);
	}
}
