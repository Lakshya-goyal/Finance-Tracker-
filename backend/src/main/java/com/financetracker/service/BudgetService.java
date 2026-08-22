package com.financetracker.service;

import com.financetracker.dto.BudgetRequest;
import com.financetracker.dto.BudgetResponse;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Category;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.entity.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         UserService userService,
                         CategoryService categoryService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public List<BudgetResponse> getBudgetsByUserId(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<BudgetResponse> responseList = new ArrayList<>();

        for (Budget budget : budgets) {
            responseList.add(mapToResponse(budget));
        }

        return responseList;
    }

    public BudgetResponse getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
        return mapToResponse(budget);
    }

    public BudgetResponse createBudget(BudgetRequest request) {
        User user = userService.getUserById(request.getUserId());
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        // Check if a budget already exists for this category, month, and year
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                request.getUserId(), request.getCategoryId(), request.getMonth(), request.getYear());

        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setAmount(request.getAmount());
        } else {
            budget = new Budget(category, user, request.getAmount(), request.getMonth(), request.getYear());
        }

        Budget saved = budgetRepository.save(budget);
        return mapToResponse(saved);
    }

    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));

        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        return mapToResponse(updated);
    }

    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    private BudgetResponse mapToResponse(Budget budget) {
        // Calculate total spent for this category in the specified month & year
        YearMonth yearMonth = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                budget.getUser().getId(), startDate, endDate);

        BigDecimal spentAmount = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getCategory().getId().equals(budget.getCategory().getId())
                    && t.getType() == TransactionType.EXPENSE) {
                spentAmount = spentAmount.add(t.getAmount());
            }
        }

        BigDecimal remainingAmount = budget.getAmount().subtract(spentAmount);
        double progressPercentage = 0.0;
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = spentAmount.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getUser().getId(),
                budget.getAmount(),
                budget.getMonth(),
                budget.getYear(),
                spentAmount,
                remainingAmount,
                progressPercentage,
                budget.getCreatedAt()
        );
    }
}
