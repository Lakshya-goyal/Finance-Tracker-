package com.financetracker.service;

import com.financetracker.dto.TransactionRequest;
import com.financetracker.dto.TransactionResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserService userService,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
        List<TransactionResponse> responseList = new ArrayList<>();

        for (Transaction t : transactions) {
            responseList.add(mapToResponse(t));
        }

        return responseList;
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return mapToResponse(transaction);
    }

    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = userService.getUserById(request.getUserId());
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        Transaction transaction = new Transaction();
        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategory(category);
        transaction.setUser(user);

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategory(category);

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getTitle(),
                t.getAmount(),
                t.getType(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getUser().getId(),
                t.getCreatedAt()
        );
    }
}
