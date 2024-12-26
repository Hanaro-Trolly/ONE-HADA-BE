package com.example.onehada.customer.account;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.transaction.TransactionService;
import com.example.onehada.customer.user.UserService;
import com.example.onehada.db.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Account", description = "계좌 관련 API")
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;
	private final JwtService jwtService;
	private final UserService userService;
	private final TransactionService transactionService;

	@Operation(summary = "사용자 계좌 목록 조회", description = "현재 로그인한 사용자의 모든 계좌 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@SecurityRequirement(name = "bearerAuth")
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
			return ResponseEntity.ok(new ApiResult(200, "OK", "계좌정보를 불러왔습니다.", accounts));
		} catch (Exception e) {
			// 실패 응답
			return ResponseEntity.badRequest()
				.body(new ApiResult(400, "BAD_REQUEST", "계좌정보를 불러올 수 없습니다.", null));
		}
	}

	@Operation(summary = "단일 계좌 조회", description = "특정 계좌의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/{accountId}")
	public ResponseEntity<?> getAccountById(@RequestHeader("Authorization") String token,
		@PathVariable("accountId") Long accountId) {
		String email = jwtService.extractEmail(token.replace("Bearer ", ""));
		Long userId = userService.getUserByEmail(email).getUserId();
		System.out.println("userId = " + userId);

		Optional<AccountDTO.accountDetailDTO> account = accountService.getMyAccountById(accountId, userId);

		return ResponseEntity.ok(new ApiResult(200, "OK", "단일 계좌 정보를 성공적으로 가져왔습니다.", account));
	}

	@Operation(summary = "계좌 존재 여부 확인", description = "특정 계좌번호의 존재 여부를 확인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/exist/{accountNumber}")
	public ResponseEntity<?> checkAccountExistence(@RequestHeader("Authorization") String token,
		@PathVariable("accountNumber") String accountNumber) {
		boolean exists = accountService.doesAccountExist(accountNumber);
		AccountDTO.accountExistDTO account = accountService.getExistAccount(accountNumber);

		if (account == null) {
			return ResponseEntity.ok(new ApiResult(200, String.valueOf(exists),
				"존재하지 않는 계좌 AccountNumber: " + accountNumber, null));
		}

		return ResponseEntity.ok(new ApiResult(200, String.valueOf(exists), "계좌 존재 여부 확인 성공", account));
	}
}
