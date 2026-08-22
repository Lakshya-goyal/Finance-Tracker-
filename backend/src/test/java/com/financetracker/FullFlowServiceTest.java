package com.financetracker;

import com.financetracker.dto.*;
import com.financetracker.entity.Category;
import com.financetracker.entity.CategoryType;
import com.financetracker.entity.TransactionType;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class FullFlowServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private DashboardService dashboardService;

    private Long userId;
    private Long foodCategoryId;
    private Long salaryCategoryId;

    @BeforeEach
    void setUp() {
        // Create user
        RegisterRequest registerReq = new RegisterRequest("Alice", "alice@example.com", "password123");
        AuthResponse auth = userService.register(registerReq);
        userId = auth.getUser().getId();

        // Ensure categories exist
        Category foodCat = categoryRepository.findByName("Food")
                .orElseGet(() -> categoryRepository.save(new Category("Food", CategoryType.EXPENSE)));
        foodCategoryId = foodCat.getId();

        Category salaryCat = categoryRepository.findByName("Salary")
                .orElseGet(() -> categoryRepository.save(new Category("Salary", CategoryType.INCOME)));
        salaryCategoryId = salaryCat.getId();
    }

    @Test
    void testTransactionCrudAndDashboard() {
        // 1. Add Income Transaction
        TransactionRequest incomeReq = new TransactionRequest();
        incomeReq.setTitle("Monthly Salary");
        incomeReq.setAmount(new BigDecimal("50000.00"));
        incomeReq.setType(TransactionType.INCOME);
        incomeReq.setDescription("Company paycheck");
        incomeReq.setTransactionDate(LocalDate.now());
        incomeReq.setCategoryId(salaryCategoryId);
        incomeReq.setUserId(userId);

        TransactionResponse incomeRes = transactionService.createTransaction(incomeReq);
        assertNotNull(incomeRes.getId());
        assertEquals("Monthly Salary", incomeRes.getTitle());

        // 2. Add Expense Transaction
        TransactionRequest expenseReq = new TransactionRequest();
        expenseReq.setTitle("Grocery Shopping");
        expenseReq.setAmount(new BigDecimal("3000.00"));
        expenseReq.setType(TransactionType.EXPENSE);
        expenseReq.setDescription("Weekly groceries");
        expenseReq.setTransactionDate(LocalDate.now());
        expenseReq.setCategoryId(foodCategoryId);
        expenseReq.setUserId(userId);

        TransactionResponse expenseRes = transactionService.createTransaction(expenseReq);
        assertNotNull(expenseRes.getId());

        // 3. Verify List
        List<TransactionResponse> list = transactionService.getTransactionsByUserId(userId);
        assertEquals(2, list.size());

        // 4. Verify Dashboard
        DashboardResponse dashboard = dashboardService.getDashboardData(userId);
        assertEquals(new BigDecimal("50000.00"), dashboard.getTotalIncome());
        assertEquals(new BigDecimal("3000.00"), dashboard.getTotalExpenses());
        assertEquals(new BigDecimal("47000.00"), dashboard.getBalance());
        assertEquals(2, dashboard.getRecentTransactions().size());
        assertEquals(1, dashboard.getCategoryExpenses().size());
        assertEquals("Food", dashboard.getCategoryExpenses().get(0).getCategoryName());

        // 5. Test Budget
        BudgetRequest budgetReq = new BudgetRequest();
        budgetReq.setCategoryId(foodCategoryId);
        budgetReq.setUserId(userId);
        budgetReq.setAmount(new BigDecimal("5000.00"));
        budgetReq.setMonth(LocalDate.now().getMonthValue());
        budgetReq.setYear(LocalDate.now().getYear());

        BudgetResponse budgetRes = budgetService.createBudget(budgetReq);
        assertNotNull(budgetRes.getId());
        assertEquals(new BigDecimal("3000.00"), budgetRes.getSpentAmount());
        assertEquals(new BigDecimal("2000.00"), budgetRes.getRemainingAmount());
        assertEquals(60.0, budgetRes.getProgressPercentage(), 0.01);
    }
}
