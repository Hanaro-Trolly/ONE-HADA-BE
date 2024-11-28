package com.example.onehada.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.repository.AccountRepository;

@Service
public class AccountService {

	private final JwtService jwtService;
	private final AccountRepository accountRepository;

	@Autowired
	public AccountService(JwtService jwtService, AccountRepository accountRepository) {
		this.jwtService = jwtService;
		this.accountRepository = accountRepository;
	}

	// JWT에서 사용자 이메일 추출
	public String getEmailFromToken(String accessToken) {
		return jwtService.extractEmail(accessToken);
	}

	// 사용자 이메일로 계좌 정보 조회
	public List<AccountDTO> getUserAccounts(String email) {
		List<Account> accounts = accountRepository.findAccountsByUserUserEmail(email);

		System.out.println("accounts = " + accounts);
		// Account 엔티티를 DTO로 변환
		return accounts.stream()
			.map(account -> new AccountDTO(
				account.getAccountId(),
				account.getAccountName(),
				account.getAccountNumber(),
				account.getBalance(),
				account.getBank()))
			.collect(Collectors.toList());
	}
}
