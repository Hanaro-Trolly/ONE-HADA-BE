package com.example.onehada.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onehada.db.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByUserUserEmail(String userEmail);
}
