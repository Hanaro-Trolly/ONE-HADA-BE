package com.example.onehada.customer.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onehada.customer.account.Account;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query("SELECT t FROM Transaction t WHERE " +
		"(t.senderAccount = :account AND LOWER(t.senderName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
		"OR (t.receiverAccount = :account AND LOWER(t.receiverName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
		"AND t.transactionDate BETWEEN :startDate AND :endDate")
	List<Transaction> findByAccountDateRangeAndKeyword(
		@Param("account") Account account,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("keyword") String keyword
	);
}
