package com.example.onehada.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.example.onehada.db.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

	@Query("SELECT c FROM Chat c ORDER BY c.createdDt DESC")
	List<Chat> findTopByOrderByCreatedDtDesc(int num);
}
