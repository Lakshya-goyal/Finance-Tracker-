import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { Transaction } from '../../models/transaction.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.css']
})
export class TransactionListComponent implements OnInit {
  transactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  categories: Category[] = [];

  // Filter States
  searchTerm: string = '';
  selectedType: string = 'ALL';
  selectedCategoryId: string = 'ALL';

  loading: boolean = true;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private transactionService: TransactionService,
    private categoryService: CategoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadTransactions();
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => (this.categories = data),
      error: () => (this.errorMessage = 'Could not load categories.')
    });
  }

  loadTransactions(): void {
    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.loading = true;
    this.transactionService.getTransactionsByUserId(user.id).subscribe({
      next: (data) => {
        this.transactions = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Could not load transactions.';
      }
    });
  }

  applyFilters(): void {
    let result = this.transactions;

    // Filter by type
    if (this.selectedType !== 'ALL') {
      result = result.filter((t) => t.type === this.selectedType);
    }

    // Filter by category
    if (this.selectedCategoryId !== 'ALL') {
      const catId = Number(this.selectedCategoryId);
      result = result.filter((t) => t.categoryId === catId);
    }

    // Filter by search text
    if (this.searchTerm.trim() !== '') {
      const search = this.searchTerm.toLowerCase().trim();
      result = result.filter(
        (t) =>
          t.title.toLowerCase().includes(search) ||
          (t.description && t.description.toLowerCase().includes(search))
      );
    }

    this.filteredTransactions = result;
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this transaction?')) {
      this.transactionService.deleteTransaction(id).subscribe({
        next: () => {
          this.successMessage = 'Transaction deleted successfully.';
          this.loadTransactions();
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: () => {
          this.errorMessage = 'Failed to delete transaction.';
        }
      });
    }
  }
}
