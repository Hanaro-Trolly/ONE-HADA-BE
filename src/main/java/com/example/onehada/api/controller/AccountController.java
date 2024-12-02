package com.example.onehada.api.controller;

import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.onehada.db.entity.Account;
import com.example.onehada.exception.account.InsufficientBalanceException;
import com.example.onehada.exception.authorization.AccessDeniedException;
import com.example.onehada.exception.user.UserNotFoundException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;
	private final JwtService jwtService;
	private final UserService userService;
	private final TransactionService transactionService;

	@Autowired
	public AccountController(AccountService accountService, JwtService jwtService, UserService userService,
		TransactionService transactionService) {
		this.accountService = accountService;
		this.jwtService = jwtService;
		this.userService = userService;
		this.transactionService = transactionService;
	}

	@GetMapping
	public ResponseEntity<?> getUserAccounts(@RequestHeader("Authorization") String token) {
		try {
			// 유효한 토큰인지 확인하고 사용자 이메일 추출
			String accessToken = token.replace("Bearer ", "");
			String email = accountService.getEmailFromToken(accessToken);

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

	@GetMapping("/{account_id}")
	public ResponseEntity<?> getAccountById(@RequestHeader("Authorization") String token,
		@PathVariable("account_id") Long accountId) {
		try {
			String email = jwtService.extractEmail(token.replace("Bearer ", ""));
			int userId = userService.getUserByEmail(email).getUserId();
			System.out.println("userId = " + userId);

			Optional<AccountDTO.accountDetailDTO> account = accountService.getAccountById(accountId, userId);

			return ResponseEntity.ok(new ApiResponse(200, "OK", "단일 계좌 정보를 성공적으로 가져왔습니다.", account));
		} catch (UserNotFoundException | AccountNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", ex.getMessage(), null));
		} catch (AccessDeniedException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ApiResponse(403, "FORBIDDEN", ex.getMessage(), null));
		}
	}

	@GetMapping("/exist/{account_id}")
	public ResponseEntity<?> checkAccountExistence(@PathVariable("account_id") Long accountId) {
		boolean exists = accountService.doesAccountExist(accountId);
		return ResponseEntity.ok(new ApiResponse(200, "OK", "계좌 존재 여부 확인 성공", exists));
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token,
		@RequestBody AccountDTO.accountTransferRequest transferRequest) {
		try {
			String email = jwtService.extractEmail(token.replace("Bearer ", ""));
			int userId = userService.getUserByEmail(email).getUserId();
			System.out.println("userId = " + userId);
			System.out.println("accountService = " + transferRequest.getFromAccountId());
			// 계좌 이체 처리
			AccountDTO.accountDetailDTO fromAccount = accountService.getAccountById(transferRequest.getFromAccountId(),
					userId)
				.orElseThrow(() -> new AccountNotFoundException("보내는 계좌를 찾을 수 없습니다."));

			System.out.println("fromAccount = " + fromAccount);

			AccountDTO.accountDetailDTO toAccount = accountService.getReceiverAccountById(transferRequest.getToAccountId())
				.orElseThrow(() -> new AccountNotFoundException("받는 계좌를 찾을 수 없습니다."));

			System.out.println("toAccount = " + toAccount);

			//DTO 생성
			AccountDTO.accountTransferDTO fromAccountDTO = AccountDTO.accountTransferDTO.builder()
				.userId(fromAccount.getUserId())
				.accountId(fromAccount.getAccountId())
				.accountNumber(fromAccount.getAccountNumber())
				.accountName(fromAccount.getAccountName())
				.accountType(fromAccount.getAccountType())
				.balance(fromAccount.getBalance())
				.bank(fromAccount.getBank())
				.build();

			AccountDTO.accountTransferDTO toAccountDTO = AccountDTO.accountTransferDTO.builder()
				.userId(toAccount.getUserId())
				.accountId(toAccount.getAccountId())
				.accountNumber(toAccount.getAccountNumber())
				.accountName(toAccount.getAccountName())
				.accountType(toAccount.getAccountType())
				.balance(toAccount.getBalance())
				.bank(toAccount.getBank())
				.build();

			System.out.println("fromAccountDTO = " + fromAccountDTO.getAccountNumber());
			System.out.println("toAccountDTO = " + toAccountDTO.getAccountNumber());

			//계좌이체
			AccountDTO.accountTransferResponse response = transactionService.transfer(fromAccountDTO, toAccountDTO,
				transferRequest.getAmount());

			// 성공 응답
			return ResponseEntity.ok(new ApiResponse(200, "OK", "계좌 이체 성공", response));
		} catch (InsufficientBalanceException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", "잔액 부족", null));
		} catch (AccountNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", "계좌를 찾을 수 없습니다.", null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", "서버 오류", null));
		}
	}

}
