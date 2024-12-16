package com.example.onehada.customer.account;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.transaction.TransactionService;
import com.example.onehada.customer.user.UserService;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.exception.authorization.AccessDeniedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;
	private final JwtService jwtService;
	private final UserService userService;
	private final TransactionService transactionService;

	@GetMapping
	public ResponseEntity<?> getUserAccounts(@RequestHeader("Authorization") String token) {
		try {
			System.out.println("token = " + token);
			// 유효한 토큰인지 확인하고 사용자 이메일 추출
			String email = accountService.getEmailFromToken(token.replace("Bearer ", ""));
			System.out.println("email = " + email);

			// 사용자 이메일로 계좌 정보 조회
			List<AccountDTO.accountsDTO> accounts = accountService.getUserAccounts(email);

			// 성공 응답
			return ResponseEntity.ok(new ApiResponse(200, "OK", "계좌정보를 불러왔습니다.", accounts));
		} catch (Exception e) {
			// 실패 응답
			return ResponseEntity.badRequest()
				.body(new ApiResponse(400, "BAD_REQUEST", "계좌정보를 불러올 수 없습니다.", null));
		}
	}

	@GetMapping("/{accountId}")
	public ResponseEntity<?> getAccountById(@RequestHeader("Authorization") String token,
		@PathVariable("accountId") Long accountId) {
		try {
			String email = jwtService.extractEmail(token.replace("Bearer ", ""));
			Long userId = userService.getUserByEmail(email).getUserId();
			System.out.println("userId = " + userId);

			Optional<AccountDTO.accountDetailDTO> account = accountService.getMyAccountById(accountId, userId);

			return ResponseEntity.ok(new ApiResponse(200, "OK", "단일 계좌 정보를 성공적으로 가져왔습니다.", account));
		} catch (AccessDeniedException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ApiResponse(403, "FORBIDDEN", ex.getMessage(), null));
		}
	}

	@GetMapping("/exist/{accountNumber}")
	public ResponseEntity<?> checkAccountExistence(@RequestHeader("Authorization") String token,
		@PathVariable("accountNumber") String accountNumber) {
		boolean exists = accountService.doesAccountExist(accountNumber);
		AccountDTO.accountExistDTO account = accountService.getExistAccount(accountNumber);
		return ResponseEntity.ok(new ApiResponse(200, String.valueOf(exists), "계좌 존재 여부 확인 성공", account));
	}
}
