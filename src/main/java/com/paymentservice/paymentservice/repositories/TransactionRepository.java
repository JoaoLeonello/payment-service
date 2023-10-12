package com.paymentservice.paymentservice.repositories;

import com.paymentservice.paymentservice.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
