import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TransactionService } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.css']
})
export class TransactionFormComponent implements OnInit {
  transactionForm: FormGroup;
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  isEditMode: boolean = false;
  transactionId: number | null = null;

  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private categoryService: CategoryService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    const today = new Date().toISOString().substring(0, 10);

    this.transactionForm = this.fb.group({
      title: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100)
      ]],
      amount: ['', [
        Validators.required,
        Validators.min(0.01),
        Validators.max(100000000)
      ]],
      type: ['EXPENSE', [Validators.required]],
      categoryId: ['', [Validators.required]],
      transactionDate: [today, [Validators.required]],
      description: ['', [Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.transactionForm.get(fieldName);
    return !!(field && field.touched && field.invalid);
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.filterCategoriesByType(this.transactionForm.get('type')?.value);
        this.checkRouteForEdit();
      },
      error: () => {
        this.errorMessage = 'Could not load categories.';
      }
    });
  }

  onTypeChange(): void {
    const selectedType = this.transactionForm.get('type')?.value;
    this.filterCategoriesByType(selectedType);
    this.transactionForm.patchValue({ categoryId: '' });
  }

  filterCategoriesByType(type: string): void {
    this.filteredCategories = this.categories.filter((c) => c.type === type);
  }

  checkRouteForEdit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.transactionId = Number(idParam);
      this.loadTransaction(this.transactionId);
    }
  }

  loadTransaction(id: number): void {
    this.loading = true;
    this.transactionService.getTransactionById(id).subscribe({
      next: (t) => {
        this.loading = false;
        this.filterCategoriesByType(t.type);
        this.transactionForm.patchValue({
          title: t.title,
          amount: t.amount,
          type: t.type,
          categoryId: t.categoryId,
          transactionDate: t.transactionDate,
          description: t.description || ''
        });
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Could not load transaction details.';
      }
    });
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      this.transactionForm.markAllAsTouched();
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formVal = this.transactionForm.value;
    const requestData = {
      title: formVal.title.trim(),
      amount: Number(formVal.amount),
      type: formVal.type,
      categoryId: Number(formVal.categoryId),
      transactionDate: formVal.transactionDate,
      description: formVal.description ? formVal.description.trim() : '',
      userId: user.id
    };

    if (this.isEditMode && this.transactionId) {
      this.transactionService.updateTransaction(this.transactionId, requestData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/transactions']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Failed to update transaction.';
        }
      });
    } else {
      this.transactionService.createTransaction(requestData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/transactions']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Failed to create transaction.';
        }
      });
    }
  }
}
