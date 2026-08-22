package com.financetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private BigDecimal thisMonthExpenses;
    private List<TransactionResponse> recentTransactions;
    private List<CategoryExpenseDto> categoryExpenses;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance,
                             BigDecimal thisMonthExpenses, List<TransactionResponse> recentTransactions,
                             List<CategoryExpenseDto> categoryExpenses) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.balance = balance;
        this.thisMonthExpenses = thisMonthExpenses;
        this.recentTransactions = recentTransactions;
        this.categoryExpenses = categoryExpenses;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getThisMonthExpenses() {
        return thisMonthExpenses;
    }

    public void setThisMonthExpenses(BigDecimal thisMonthExpenses) {
        this.thisMonthExpenses = thisMonthExpenses;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<CategoryExpenseDto> getCategoryExpenses() {
        return categoryExpenses;
    }

    public void setCategoryExpenses(List<CategoryExpenseDto> categoryExpenses) {
        this.categoryExpenses = categoryExpenses;
    }
}
