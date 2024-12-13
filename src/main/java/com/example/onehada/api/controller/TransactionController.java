package com.example.onehada.api.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.auth.service.JwtService;
import com.example.onehada.api.service.AccountService;
import com.example.onehada.api.service.TransactionService;
import com.example.onehada.api.service.UserService;
import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.db.dto.TransactionDTO;
import com.example.onehada.exception.InvalidDateRangeException;
import com.example.onehada.exception.account.AccountNotFoundException;
import com.example.onehada.exception.account.InsufficientBalanceException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
	private final TransactionService transactionService;
	private final JwtService jwtService;
	private final UserService userService;
	private final AccountService accountService;

	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token,
		@RequestBody AccountDTO.accountTransferRequest transferRequest) {
		try {
			String email = jwtService.extractEmail(token.replace("Bearer ", ""));
			Long userId = userService.getUserByEmail(email).getUserId();

			//계좌이체
			AccountDTO.accountTransferResponse response = transactionService.transfer(transferRequest, userId);

			// 성공 응답
			return ResponseEntity.ok(new ApiResponse(200, "OK", "계좌 이체 성공", response));
		} catch (InsufficientBalanceException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", "잔액 부족", null));
		} catch (javax.security.auth.login.AccountNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", "계좌를 찾을 수 없습니다.", null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", "서버 오류", null));
		}
	}

	@GetMapping("/{accountId}")
	public ResponseEntity<?> getTransactions(@PathVariable("accountId") Long accountId,
		@RequestBody TransactionDTO.transactionRequest request) {
		try {
			List<TransactionDTO.transactionDTO> transactions = transactionService.getTransactions(accountId, request);

			if (transactions.isEmpty()) {
				return ResponseEntity.ok(new ApiResponse(200, "OK", "거래 내역이 없습니다.", Collections.emptyList()));
			}
			return ResponseEntity.ok(new ApiResponse(200, "OK", "거래 내역을 불러왔습니다.", transactions));
		} catch (AccountNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse(404, "NOT_FOUND", ex.getMessage(), null));
		} catch (InvalidDateRangeException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", ex.getMessage(), null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", "서버 오류", null));
		}
	}
}
