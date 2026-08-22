package com.financetracker.repository;

import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(Long userId, Long categoryId);

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
