package com.example.onehada.customer.transaction;

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

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.UserService;
import com.example.onehada.customer.account.AccountDTO;
import com.example.onehada.db.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {
	private final TransactionService transactionService;
	private final JwtService jwtService;
	private final UserService userService;

	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token,
		@RequestBody AccountDTO.accountTransferRequest transferRequest) {
		String email = jwtService.extractEmail(token.replace("Bearer ", ""));
		Long userId = userService.getUserByEmail(email).getUserId();

		//계좌이체
		AccountDTO.accountTransferResponse response = transactionService.transfer(transferRequest, userId);

		// 성공 응답
		return ResponseEntity.ok(new ApiResponse(200, "OK", "계좌 이체 성공", response));
	}
	@GetMapping("/{accountId}")
	public ResponseEntity<?> getTransactions(@PathVariable("accountId") Long accountId,
		@RequestBody TransactionDTO.transactionRequest request) {
		List<TransactionDTO.transactionDTO> transactions = transactionService.getTransactions(accountId, request);

		if (transactions.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse(200, "OK", "거래 내역이 없습니다.", Collections.emptyList()));
		}
		return ResponseEntity.ok(new ApiResponse(200, "OK", "거래 내역을 불러왔습니다.", transactions));
	}
}
