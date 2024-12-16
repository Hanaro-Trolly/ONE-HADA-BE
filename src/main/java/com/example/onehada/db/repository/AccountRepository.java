package com.example.onehada.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onehada.db.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	// @Query("SELECT a FROM Account a WHERE a.user.userEmail = :userEmail")
	List<Account> findAccountsByUserUserEmail(String userEmail);

	Optional<Account> findByAccountId(Long accountId);

	boolean existsByAccountNumber(String accountNumber);
	Optional<Account> findByAccountNumber(String accountNumber);
}
