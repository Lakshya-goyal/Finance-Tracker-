package com.financetracker.controller;

import com.financetracker.dto.BudgetRequest;
import com.financetracker.dto.BudgetResponse;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByUserId(@PathVariable Long userId) {
        List<BudgetResponse> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long id,
                                                       @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
