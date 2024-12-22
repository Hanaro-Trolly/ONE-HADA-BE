package com.example.onehada.customer.user;

import org.springframework.stereotype.Service;

import com.example.onehada.auth.service.JwtService;
import com.example.onehada.customer.account.AccountRepository;
import com.example.onehada.customer.consultation.ConsultationRepository;
import com.example.onehada.customer.history.HistoryRepository;
import com.example.onehada.customer.shortcut.ShortcutRepository;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserInfoService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final HistoryRepository historyRepository;
	private final ShortcutRepository shortcutRepository;
	private final ConsultationRepository consultationRepository;
	private final AccountRepository accountRepository;

	public UserInfoService(JwtService jwtService, UserRepository userRepository, HistoryRepository historyRepository,
		ShortcutRepository shortcutRepository, ConsultationRepository consultationRepository,
		AccountRepository accountRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.historyRepository = historyRepository;
		this.shortcutRepository = shortcutRepository;
		this.consultationRepository = consultationRepository;
		this.accountRepository = accountRepository;
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

		consultationRepository.deleteAllByUser(user);
		shortcutRepository.deleteAllByUser(user);
		historyRepository.deleteAllByUser(user);
		accountRepository.deleteAllByUser(user);
		userRepository.deleteById(userId);
	}
}
