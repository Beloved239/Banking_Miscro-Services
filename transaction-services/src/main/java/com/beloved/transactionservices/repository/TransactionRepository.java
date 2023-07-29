package com.beloved.transactionservices.repository;


import com.beloved.transactionservices.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
