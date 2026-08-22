package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private BigDecimal amount;
    private int month;
    private int year;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double progressPercentage;
    private LocalDateTime createdAt;

    public BudgetResponse() {
    }

    public BudgetResponse(Long id, Long categoryId, String categoryName, Long userId,
                          BigDecimal amount, int month, int year, BigDecimal spentAmount,
                          BigDecimal remainingAmount, double progressPercentage, LocalDateTime createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
