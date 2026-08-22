package com.financetracker.service;

import com.financetracker.dto.CategoryExpenseDto;
import com.financetracker.dto.DashboardResponse;
import com.financetracker.dto.TransactionResponse;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public DashboardResponse getDashboardData(Long userId) {
        List<Transaction> allTransactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Map<String, BigDecimal> expenseByCategoryMap = new HashMap<>();

        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();
        BigDecimal thisMonthExpenses = BigDecimal.ZERO;

        for (Transaction t : allTransactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpenses = totalExpenses.add(t.getAmount());

                // Group expenses by Category
                String categoryName = t.getCategory().getName();
                BigDecimal currentCatAmount = expenseByCategoryMap.getOrDefault(categoryName, BigDecimal.ZERO);
                expenseByCategoryMap.put(categoryName, currentCatAmount.add(t.getAmount()));

                // Check if transaction is in current month
                if (!t.getTransactionDate().isBefore(startOfMonth) && !t.getTransactionDate().isAfter(endOfMonth)) {
                    thisMonthExpenses = thisMonthExpenses.add(t.getAmount());
                }
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        // Recent 5 transactions
        List<TransactionResponse> recentTransactions = new ArrayList<>();
        int limit = Math.min(allTransactions.size(), 5);
        for (int i = 0; i < limit; i++) {
            Transaction t = allTransactions.get(i);
            recentTransactions.add(new TransactionResponse(
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
            ));
        }

        // Category Expenses with percentages
        List<CategoryExpenseDto> categoryExpenses = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : expenseByCategoryMap.entrySet()) {
            double percentage = 0.0;
            if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                percentage = entry.getValue()
                        .divide(totalExpenses, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }
            categoryExpenses.add(new CategoryExpenseDto(entry.getKey(), entry.getValue(), percentage));
        }

        return new DashboardResponse(
                totalIncome,
                totalExpenses,
                balance,
                thisMonthExpenses,
                recentTransactions,
                categoryExpenses
        );
    }
}
