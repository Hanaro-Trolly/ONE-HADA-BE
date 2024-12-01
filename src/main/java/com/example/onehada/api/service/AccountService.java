package com.example.onehada.api.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onehada.api.auth.service.AuthService;
import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.repository.AccountRepository;

@Service
public class AccountService {

	private final JwtService jwtService;
	private final AccountRepository accountRepository;
	private final AuthService authService;

	@Autowired
	public AccountService(JwtService jwtService, AccountRepository accountRepository,AuthService authService) {
		this.jwtService = jwtService;
		this.accountRepository = accountRepository;
		this.authService = authService;
	}

	// JWT에서 사용자 이메일 추출
	public String getEmailFromToken(String accessToken) {
		return jwtService.extractEmail(accessToken);
	}

	// 사용자 이메일로 계좌 정보 조회
	public List<AccountDTO.accountsDTO> getUserAccounts(String email) {
		List<Account> accounts = accountRepository.findAccountsByUserUserEmail(email);

		// Account 엔티티를 DTO로 변환(Construct 사용)
		return accounts.stream()
			.map(account -> new AccountDTO.accountsDTO(
				account.getAccountId(),
				account.getAccountName(),
				account.getAccountNumber(),
				account.getBalance(),
				account.getBank()))
			.collect(Collectors.toList());
	}
	public AccountDTO.accountDetailDTO getAccountById(Long accountId ,int userId) throws AccountNotFoundException {
		System.out.println("AccountService.getAccountById");
		authService.validateAccountOwnership(accountId, userId);

		Account account = accountRepository.findByAccountId(accountId)
			.orElseThrow(() -> new AccountNotFoundException("존재하지 않는 계좌 id 입니다. ID : " + accountId));
		//build 사용
		return AccountDTO.accountDetailDTO.builder()
			.userId(account.getUser().getUserId())
			.accountId(account.getAccountId())
			.accountName(account.getAccountName())
			.accountNumber(account.getAccountNumber())
			.accountType(account.getAccountType())
			.balance(account.getBalance())
			.bank(account.getBank())
			.build();
	}
}
