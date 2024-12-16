package com.example.onehada.db.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onehada.db.entity.Account;
import com.example.onehada.db.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query("SELECT t FROM Transaction t WHERE " +
		"(t.senderAccount = :account OR t.receiverAccount = :account) " +
		"AND t.transactionDate BETWEEN :startDate AND :endDate " +
		"AND (LOWER(t.senderName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(t.receiverName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Transaction> findByAccountDateRangeAndKeyword(
		@Param("account") Account account,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("keyword") String keyword
	);
}
