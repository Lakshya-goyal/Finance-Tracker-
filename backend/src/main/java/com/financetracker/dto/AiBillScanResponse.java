package com.financetracker.dto;

import com.financetracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) returned to the frontend after Gemini AI
 * analyzes and categorizes a bill/receipt image or statement.
 */
public class AiBillScanResponse {

    private String title;
    private BigDecimal amount;
    private TransactionType type;
    private Long categoryId;
    private String categoryName;
    private LocalDate transactionDate;
    private String description;
    private String rawSummary;

    public AiBillScanResponse() {
    }

    public AiBillScanResponse(String title, BigDecimal amount, TransactionType type,
                              Long categoryId, String categoryName, LocalDate transactionDate,
                              String description, String rawSummary) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.transactionDate = transactionDate;
        this.description = description;
        this.rawSummary = rawSummary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRawSummary() {
        return rawSummary;
    }

    public void setRawSummary(String rawSummary) {
        this.rawSummary = rawSummary;
    }
}
