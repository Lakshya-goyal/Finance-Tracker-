package com.financetracker.controller;

import com.financetracker.dto.AiBillScanResponse;
import com.financetracker.dto.AiTextScanRequest;
import com.financetracker.service.AiBillScannerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for AI-powered bill scanning and categorization
 * using Google Gemini AI.
 */
@RestController
@RequestMapping("/api/ai")
public class AiBillScannerController {

    private final AiBillScannerService aiBillScannerService;

    public AiBillScannerController(AiBillScannerService aiBillScannerService) {
        this.aiBillScannerService = aiBillScannerService;
    }

    /**
     * Upload an image of a receipt/bill and receive AI categorized transaction data
     */
    @PostMapping(value = "/scan-receipt", consumes = "multipart/form-data")
    public ResponseEntity<AiBillScanResponse> scanReceiptImage(@RequestParam("file") MultipartFile file) {
        AiBillScanResponse response = aiBillScannerService.scanReceiptImage(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Send a text-based bill statement, SMS alert, or invoice text and receive AI categorization
     */
    @PostMapping("/scan-statement")
    public ResponseEntity<AiBillScanResponse> scanBillStatement(@Valid @RequestBody AiTextScanRequest request) {
        AiBillScanResponse response = aiBillScannerService.scanBillStatementText(request.getStatement());
        return ResponseEntity.ok(response);
    }
}
