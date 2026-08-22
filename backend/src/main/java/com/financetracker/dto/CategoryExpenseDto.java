package com.financetracker.dto;

import java.math.BigDecimal;

public class CategoryExpenseDto {

    private String categoryName;
    private BigDecimal amount;
    private double percentage;

    public CategoryExpenseDto() {
    }

    public CategoryExpenseDto(String categoryName, BigDecimal amount, double percentage) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.percentage = percentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
