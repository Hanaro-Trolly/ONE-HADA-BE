package com.example.onehada.api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.Transaction;
import com.example.onehada.db.entity.User;
import com.example.onehada.db.repository.AccountRepository;
import com.example.onehada.db.repository.TransactionRepository;
import com.example.onehada.db.repository.UserRepository;
import com.example.onehada.exception.account.InsufficientBalanceException;
import com.example.onehada.exception.user.UserNotFoundException;

@Service
public class TransactionService {
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public TransactionService(AccountRepository accountRepository,UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public AccountDTO.accountTransferResponse transfer(
		AccountDTO.accountTransferDTO fromAccountDto,
		AccountDTO.accountTransferDTO toAccountDto,
		Long amount) {
		if (fromAccountDto.getBalance() < amount) {
			throw new InsufficientBalanceException("잔액이 부족합니다.");
		}

		// DTO에서 잔액 업데이트
		fromAccountDto.updateBalance(-amount);
		toAccountDto.updateBalance(amount);

		// User 객체 조회
		User fromUser = userRepository.findById(fromAccountDto.getUserId())
			.orElseThrow(() -> new UserNotFoundException("보내는 계좌의 사용자를 찾을 수 없습니다."));
		User toUser = userRepository.findById(toAccountDto.getUserId())
			.orElseThrow(() -> new UserNotFoundException("받는 계좌의 사용자를 찾을 수 없습니다."));

		// 엔티티 변환 후 저장
		Account fromAccount = fromAccountDto.toEntity(fromUser);
		Account toAccount = toAccountDto.toEntity(toUser);

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);

		Transaction transaction = Transaction.builder()
			.senderAccount(fromAccount)
			.receiverAccount(toAccount)
			.amount(amount)
			.senderName(fromAccount.getAccountName())
			.receiverName(toAccount.getAccountName())
			.build();

		return AccountDTO.accountTransferResponse.builder()
			.amount(amount)
			.transactionDate(transaction.getTransactionDate())
			.senderView(fromAccount.getAccountName())
			.receiverView(toAccount.getAccountName())
			.build();
	}
}
