package com.example.onehada.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
