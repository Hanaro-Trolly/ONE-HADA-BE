package com.example.onehada.customer.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onehada.customer.user.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	// @Query("SELECT a FROM Account a WHERE a.user.userEmail = :userEmail")
	List<Account> findAccountsByUserUserEmail(String userEmail);

	Optional<Account> findByAccountId(Long accountId);

	boolean existsByAccountNumber(String accountNumber);
	Optional<Account> findByAccountNumber(String accountNumber);

	void deleteAllByUser(User user);
}
