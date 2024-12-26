package com.example.onehada.customer.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.account.Account;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.transaction.TransactionRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserInfoService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	public UserInfoService(JwtService jwtService, UserRepository userRepository,
		AccountRepository accountRepository, TransactionRepository transactionRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
	}

	private String getEmailFromToken(String token) {
		String accessToken = token.replace("Bearer ", "");
		return jwtService.extractEmail(accessToken);
	}

	private Long getUserIdFromToken(String token) {
		String accessToken = token.replace("Bearer ", "");
		return jwtService.extractUserId(accessToken);
	}

	public UserInfoDTO getUserInfo(String token)
	{
		String email = getEmailFromToken(token);
		User user = userRepository.findByUserEmail(email)
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		System.out.println("User: " + user);
		return UserInfoDTO.builder()
			.userName(user.getUserName())
			.userEmail(user.getUserEmail())
			.userPhone(user.getPhoneNumber())
			.userAddress(user.getUserAddress())
			.userBirth(user.getUserBirth())
			.userGender(user.getUserGender())
			.build();
	}

	@Transactional
	public UserUpdateDTO updateUser(String token, UserUpdateDTO userUpdate) {

		Long userId = getUserIdFromToken(token);
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		String updateUserPhone = userUpdate.getUserPhone();
		String updateUserAddress = userUpdate.getUserAddress();
		if (updateUserPhone.isEmpty() && updateUserAddress.isEmpty()) {
			throw new BadRequestException("잘못된 형식의 데이터입니다.");
		}
		if (!updateUserPhone.isEmpty()) {
			user.setPhoneNumber(updateUserPhone);
		}
		if (!updateUserAddress.isEmpty()) {
			user.setUserAddress(updateUserAddress);
		}

		userRepository.save(user);
		return new UserUpdateDTO(user.getPhoneNumber(), user.getUserAddress());
	}

	@Transactional
	public void deleteUser(String token) {
		Long userId = getUserIdFromToken(token);
		User user = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		List<Account> userAccounts = accountRepository.findAccountsByUserUserEmail(user.getUserEmail());
		for (Account account : userAccounts) {
			transactionRepository.updateSenderAccountToNull(account.getAccountId());
			transactionRepository.updateReceiverAccountToNull(account.getAccountId());
		}
		accountRepository.deleteAllByUser(user);
		userRepository.deleteById(userId);
	}
}
