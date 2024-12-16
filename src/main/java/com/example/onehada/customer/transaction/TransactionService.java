package com.example.onehada.customer.transaction;

import static java.time.LocalDateTime.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onehada.customer.account.AccountService;
import com.example.onehada.customer.account.AccountDTO;
import com.example.onehada.customer.account.Account;
import com.example.onehada.customer.user.User;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.user.UserRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
	private final AccountService accountService;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final TransactionRepository transactionRepository;

	@Transactional
	public AccountDTO.accountTransferResponse transfer(
		AccountDTO.accountTransferRequest transferRequest,
		Long userId) throws NotFoundException {
		AccountDTO.accountDetailDTO fromAccountDto = accountService.getMyAccountById(transferRequest.getFromAccountId(),
				userId)
			.orElseThrow(() -> new NotFoundException("보내는 계좌를 찾을 수 없습니다."));
		AccountDTO.accountDetailDTO toAccountDto = accountService.getReceiverAccountById(transferRequest.getToAccountId())
			.orElseThrow(() -> new NotFoundException("받는 계좌를 찾을 수 없습니다."));
		Long amount = transferRequest.getAmount();

		if (fromAccountDto.getBalance() < amount) {
			throw new BadRequestException("잔액이 부족합니다.");
		}
		Long postReceiverBalance = toAccountDto.getBalance();
		Long postSenderBalance = fromAccountDto.getBalance();
		fromAccountDto.updateBalance(-amount);
		toAccountDto.updateBalance(amount);

		User fromUser = userRepository.findById(fromAccountDto.getUserId())
			.orElseThrow(() -> new NotFoundException("보내는 계좌의 사용자를 찾을 수 없습니다."));
		User toUser = userRepository.findById(toAccountDto.getUserId())
			.orElseThrow(() -> new NotFoundException("받는 계좌의 사용자를 찾을 수 없습니다."));

		Account fromAccount = fromAccountDto.toEntity(fromUser);
		Account toAccount = toAccountDto.toEntity(toUser);

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);

		Transaction transaction = Transaction.builder()
			.senderAccount(fromAccount)
			.receiverAccount(toAccount)
			.transactionDate(now())
			.postReceiverBalance(postReceiverBalance)
			.postSenderBalance(postSenderBalance)
			.amount(amount)
			.senderName(transferRequest.getSenderMessage())
			.receiverName(transferRequest.getReceiverMessage())
			.build();

		transactionRepository.save(transaction);

		return AccountDTO.accountTransferResponse.builder()
			.amount(amount)
			.transactionDate(transaction.getTransactionDate())
			.senderView(transferRequest.getSenderMessage())
			.receiverView(transferRequest.getReceiverMessage())
			.build();
	}

	public List<TransactionDTO.transactionDTO> getTransactions(Long accountId,
		TransactionDTO.transactionRequest request) throws NotFoundException,BadRequestException{
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new NotFoundException("존재하지 않는 계좌 ID입니다."));

		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new BadRequestException("시작 날짜가 종료 날짜보다 클 수 없습니다.");
		}

		// 거래 내역 조회
		List<Transaction> transactions = transactionRepository.findByAccountDateRangeAndKeyword(
			account,
			request.getStartDate(),
			request.getEndDate(),
			request.getKeyword()
		);

		// DTO 변환
		return transactions.stream()
			.map(transaction -> TransactionDTO.transactionDTO.fromEntity(transaction, account))
			.collect(Collectors.toList());
	}
}
