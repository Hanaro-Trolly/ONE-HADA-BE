package com.example.onehada.db.repository;

import com.example.onehada.db.entity.History;
import com.example.onehada.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findByUser(User user);

	List<History> findHistoryByUserUserId(Long userId);
}
