package com.example.onehada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.onehada.api.service.AccountService;
import com.example.onehada.db.dto.AccountDTO;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@Test
	public void testGetUserAccounts_Success() throws Exception {
		// Given
		String accessToken = "valid-access-token";
		String email = "test@test.com";

		System.out.println("accessToken = " + accountService.getEmailFromToken(accessToken));



		List<AccountDTO> accounts = List.of(
			new AccountDTO(1, "입출금", "123456789", 1000000, "은행A"),
			new AccountDTO(2, "적금", "987654321", 500000, "은행B")
		);

		// Mock Service
		Mockito.when(accountService.getEmailFromToken(accessToken)).thenReturn(email);
		Mockito.when(accountService.getUserAccounts(email)).thenReturn(accounts);

		// When & Then
		mockMvc.perform(get("/api/accounts")
				.header("Authorization", accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.message").value("계좌정보를 불러왔습니다."))
			.andExpect(jsonPath("$.data[0].accountId").value(1))
			.andExpect(jsonPath("$.data[0].accountName").value("입출금"))
			.andExpect(jsonPath("$.data[0].accountNumber").value("123456789"))
			.andExpect(jsonPath("$.data[0].balance").value(1000000))
			.andExpect(jsonPath("$.data[0].bank").value("은행A"));
	}
}
