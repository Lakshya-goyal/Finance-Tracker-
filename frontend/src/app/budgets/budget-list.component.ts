import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BudgetService } from '../services/budget.service';
import { CategoryService } from '../services/category.service';
import { AuthService } from '../services/auth.service';
import { Budget } from '../models/budget.model';
import { Category } from '../models/category.model';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './budget-list.component.html',
  styleUrls: ['./budget-list.component.css']
})
export class BudgetListComponent implements OnInit {
  budgets: Budget[] = [];
  expenseCategories: Category[] = [];
  budgetForm: FormGroup;
  editingBudgetId: number | null = null;

  months = [
    { value: 1, name: 'January' },
    { value: 2, name: 'February' },
    { value: 3, name: 'March' },
    { value: 4, name: 'April' },
    { value: 5, name: 'May' },
    { value: 6, name: 'June' },
    { value: 7, name: 'July' },
    { value: 8, name: 'August' },
    { value: 9, name: 'September' },
    { value: 10, name: 'October' },
    { value: 11, name: 'November' },
    { value: 12, name: 'December' }
  ];

  loading: boolean = true;
  saving: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private categoryService: CategoryService,
    private authService: AuthService
  ) {
    const now = new Date();
    this.budgetForm = this.fb.group({
      categoryId: ['', [Validators.required]],
      amount: ['', [
        Validators.required,
        Validators.min(0.01),
        Validators.max(100000000)
      ]],
      month: [now.getMonth() + 1, [
        Validators.required,
        Validators.min(1),
        Validators.max(12)
      ]],
      year: [now.getFullYear(), [
        Validators.required,
        Validators.min(2000),
        Validators.max(2100)
      ]]
    });
  }

  ngOnInit(): void {
    this.loadExpenseCategories();
    this.loadBudgets();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.budgetForm.get(fieldName);
    return !!(field && field.touched && field.invalid);
  }

  loadExpenseCategories(): void {
    this.categoryService.getCategories('EXPENSE').subscribe({
      next: (data) => (this.expenseCategories = data),
      error: () => (this.errorMessage = 'Could not load expense categories.')
    });
  }

  loadBudgets(): void {
    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.loading = true;
    this.budgetService.getBudgetsByUserId(user.id).subscribe({
      next: (data) => {
        this.budgets = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Could not load budgets.';
      }
    });
  }

  onSubmit(): void {
    if (this.budgetForm.invalid) {
      this.budgetForm.markAllAsTouched();
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.saving = true;
    this.errorMessage = '';

    const formVal = this.budgetForm.value;
    const requestData = {
      categoryId: Number(formVal.categoryId),
      userId: user.id,
      amount: Number(formVal.amount),
      month: Number(formVal.month),
      year: Number(formVal.year)
    };

    if (this.editingBudgetId) {
      this.budgetService.updateBudget(this.editingBudgetId, requestData).subscribe({
        next: () => {
          this.saving = false;
          this.successMessage = 'Budget updated successfully!';
          this.cancelEdit();
          this.loadBudgets();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update budget.';
        }
      });
    } else {
      this.budgetService.createBudget(requestData).subscribe({
        next: () => {
          this.saving = false;
          this.successMessage = 'Budget saved successfully!';
          this.budgetForm.patchValue({ amount: '' });
          this.loadBudgets();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to save budget.';
        }
      });
    }
  }

  onEdit(budget: Budget): void {
    this.editingBudgetId = budget.id;
    this.budgetForm.patchValue({
      categoryId: budget.categoryId,
      amount: budget.amount,
      month: budget.month,
      year: budget.year
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingBudgetId = null;
    const now = new Date();
    this.budgetForm.patchValue({
      categoryId: '',
      amount: '',
      month: now.getMonth() + 1,
      year: now.getFullYear()
    });
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this budget?')) {
      this.budgetService.deleteBudget(id).subscribe({
        next: () => {
          this.successMessage = 'Budget deleted successfully.';
          this.loadBudgets();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: () => {
          this.errorMessage = 'Failed to delete budget.';
        }
      });
    }
  }

  getMonthName(monthNum: number): string {
    const m = this.months.find((item) => item.value === monthNum);
    return m ? m.name : String(monthNum);
  }
}
