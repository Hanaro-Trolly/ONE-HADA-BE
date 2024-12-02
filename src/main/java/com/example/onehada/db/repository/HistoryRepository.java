package com.example.onehada.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findHistoryByUserUserId(int userId);
}
