package com.example.onehada.customer.account;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.AuthService;
import com.example.onehada.auth.service.JwtService;
import com.example.onehada.exception.NotFoundException;

@Service
public class AccountService {

	private final JwtService jwtService;
	private final AccountRepository accountRepository;
	private final AuthService authService;

	@Autowired
	public AccountService(JwtService jwtService, AccountRepository accountRepository, AuthService authService) {
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
				account.getAccountType(),
				account.getAccountName(),
				account.getAccountNumber(),
				account.getBalance(),
				account.getBank()))
			.collect(Collectors.toList());
	}

	public Optional<AccountDTO.accountDetailDTO> getMyAccountById(Long accountId, Long userId) throws
		NotFoundException {

		authService.validateAccountOwnership(accountId, userId);

		// Account 조회
		return accountRepository.findByAccountId(accountId)
			.map(account -> AccountDTO.accountDetailDTO.builder()
				.userId(account.getUser().getUserId())
				.accountId(account.getAccountId())
				.accountName(account.getAccountName())
				.accountNumber(account.getAccountNumber())
				.accountType(account.getAccountType())
				.balance(account.getBalance())
				.bank(account.getBank())
				.build());
	}
	public Optional<AccountDTO.accountDetailDTO> getReceiverAccountById(Long accountId) throws NotFoundException{
		return accountRepository.findByAccountId(accountId)
			.map(account -> AccountDTO.accountDetailDTO.builder()
				.userId(account.getUser().getUserId())
				.accountId(account.getAccountId())
				.accountName(account.getAccountName())
				.accountNumber(account.getAccountNumber())
				.accountType(account.getAccountType())
				.balance(account.getBalance())
				.bank(account.getBank())
				.build());
	}

	public boolean doesAccountExist(String accountNumber) {
		return accountRepository.existsByAccountNumber(accountNumber);
	}

	public AccountDTO.accountExistDTO getExistAccount(String accountNumber) {
		Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);

		return accountOptional.map(account -> AccountDTO.accountExistDTO.builder()
				.accountId(account.getAccountId())
				.userName(account.getUser().getUserName())
				.bank(account.getBank())
				.build())
			//Todo 리팩토링 toAccountNotFound
			.orElseThrow(() -> new NotFoundException(" AccountNumber: " + accountNumber));
	}
}
