package com.example.onehada.api.controller;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.api.service.AccountService;
import com.example.onehada.db.dto.AccountDTO;
import com.example.onehada.db.dto.ApiResponse;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	public ResponseEntity<?> getUserAccounts(@RequestHeader("Authorization") String Token) {
		try {
			// 유효한 토큰인지 확인하고 사용자 이메일 추출
			String accessToken = Token.replace("Bearer ", "");
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
	public ResponseEntity<?> getAccountById(@RequestHeader("Authorization") String Token,@PathVariable("account_id") Long accountId) {
		try {
			String accessToken = Token.replace("Bearer ", "");

			//Todo 토큰으로
			AccountDTO.accountDetailDTO account = accountService.getAccountById(accountId);
			return ResponseEntity.ok(new ApiResponse(200, "OK", "단일 계좌 정보를 성공적으로 가져왔습니다.", account));
		} catch (AccountNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse(400, "BAD_REQUEST", ex.getMessage(), null));
		}
	}
}
