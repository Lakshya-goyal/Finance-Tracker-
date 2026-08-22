package com.financetracker.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for analyzing text-based bill statements,
 * SMS alerts, or pasted receipts.
 */
public class AiTextScanRequest {

    @NotBlank(message = "Bill statement or receipt text is required")
    private String statement;

    public AiTextScanRequest() {
    }

    public AiTextScanRequest(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
