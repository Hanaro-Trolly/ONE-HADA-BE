package com.example.onehada.customer.transaction;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.user.UserService;
import com.example.onehada.customer.account.AccountDTO;
import com.example.onehada.db.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "거래 관련 API")
@RequestMapping("/api/transaction")
public class TransactionController {
	private final TransactionService transactionService;
	private final JwtService jwtService;
	private final UserService userService;

	@Operation(summary = "계좌 이체", description = "한 계좌에서 다른 계좌로 금액을 이체합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이체 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token,
		@RequestBody AccountDTO.accountTransferRequest transferRequest) {
		String email = jwtService.extractEmail(token.replace("Bearer ", ""));
		Long userId = userService.getUserByEmail(email).getUserId();

		//계좌이체
		AccountDTO.accountTransferResponse response = transactionService.transfer(transferRequest, userId);

		// 성공 응답
		return ResponseEntity.ok(new ApiResult(200, "OK", "계좌 이체 성공", response));
	}

	@Operation(summary = "거래 내역 조회", description = "특정 계좌의 거래 내역을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	@PostMapping("/{accountId}")
	public ResponseEntity<?> getTransactions(@PathVariable("accountId") Long accountId,
		@RequestBody TransactionDTO.transactionRequest request) {
		List<TransactionDTO.transactionDTO> transactions = transactionService.getTransactions(accountId, request);

		if (transactions.isEmpty()) {
			return ResponseEntity.ok(new ApiResult(200, "OK", "거래 내역이 없습니다.", Collections.emptyList()));
		}
		return ResponseEntity.ok(new ApiResult(200, "OK", "거래 내역을 불러왔습니다.", transactions));
	}
}
