package com.example.onehada.customer.history;

import com.example.onehada.customer.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findByUser(User user);

	List<History> findHistoryByUserUserIdOrderByHistoryIdDesc(Long userId);

	History findHistoryByHistoryIdAndUserUserId(Long historyId, Long user_userId);

	void deleteAllByUser(User user);
}
