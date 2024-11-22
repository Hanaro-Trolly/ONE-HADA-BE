package com.example.onehada.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.RedisService;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

	@Autowired
	private RedisService redisService;

	@PostMapping("/set")
	public String setValue(@RequestParam("key") String key, @RequestParam("value") String value) {
		redisService.saveValue(key, value);
		return "Value saved successfully!";
	}

	@GetMapping("/get")
	public String getValue(@RequestParam("key") String key) {
		String value = redisService.getValue(key);
		return value != null ? value : "No value found for the given key!";
	}

	@DeleteMapping("/delete")
	public String deleteValue(@RequestParam("key") String key) {
		redisService.deleteValue(key);
		return "Value deleted successfully!";
	}
}
