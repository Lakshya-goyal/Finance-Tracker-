import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AiService } from '../services/ai.service';
import { CategoryService } from '../services/category.service';
import { TransactionService } from '../services/transaction.service';
import { AuthService } from '../services/auth.service';
import { Category } from '../models/category.model';
import { AiBillScanResponse } from '../models/ai-scan.model';
import { TransactionRequest } from '../models/transaction.model';

@Component({
  selector: 'app-ai-scanner',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './ai-scanner.component.html',
  styleUrls: ['./ai-scanner.component.css']
})
export class AiScannerComponent implements OnInit {
  activeTab: 'image' | 'text' = 'image';

  // Image Upload State
  selectedFile: File | null = null;
  imagePreviewUrl: string | null = null;
  isDragging: boolean = false;

  // Text Statement State
  statementText: string = '';

  // AI Processing State
  scanning: boolean = false;
  scanProgressText: string = 'Analyzing with Gemini AI...';
  scanSuccess: boolean = false;
  aiResponse: AiBillScanResponse | null = null;
  errorMessage: string = '';
  successMessage: string = '';

  // Review & Edit Form
  reviewForm: FormGroup;
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  saving: boolean = false;

  constructor(
    private fb: FormBuilder,
    private aiService: AiService,
    private categoryService: CategoryService,
    private transactionService: TransactionService,
    private authService: AuthService,
    private router: Router
  ) {
    const today = new Date().toISOString().substring(0, 10);

    this.reviewForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      amount: ['', [Validators.required, Validators.min(0.01), Validators.max(100000000)]],
      type: ['EXPENSE', [Validators.required]],
      categoryId: ['', [Validators.required]],
      transactionDate: [today, [Validators.required]],
      description: ['', [Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.filterCategoriesByType(this.reviewForm.get('type')?.value || 'EXPENSE');
      },
      error: () => {
        this.errorMessage = 'Failed to load categories.';
      }
    });
  }

  filterCategoriesByType(type: string): void {
    this.filteredCategories = this.categories.filter((c) => c.type === type);
  }

  onTypeChange(): void {
    const selectedType = this.reviewForm.get('type')?.value;
    this.filterCategoriesByType(selectedType);
    if (this.filteredCategories.length > 0) {
      this.reviewForm.patchValue({ categoryId: this.filteredCategories[0].id });
    }
  }

  // File Handling
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  handleFile(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.errorMessage = 'Please upload a valid image file (PNG, JPG, JPEG, WEBP).';
      return;
    }
    this.errorMessage = '';
    this.selectedFile = file;

    // Create image preview
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreviewUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  clearSelectedFile(): void {
    this.selectedFile = null;
    this.imagePreviewUrl = null;
    this.scanSuccess = false;
    this.aiResponse = null;
  }

  // Quick sample text selection
  setSampleStatement(sample: string): void {
    this.statementText = sample;
  }

  // AI Trigger
  startScan(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.activeTab === 'image') {
      if (!this.selectedFile) {
        this.errorMessage = 'Please select or drop an image first.';
        return;
      }
      this.scanImage();
    } else {
      if (!this.statementText.trim()) {
        this.errorMessage = 'Please enter or paste bill statement text.';
        return;
      }
      this.scanText();
    }
  }

  private scanImage(): void {
    if (!this.selectedFile) return;

    this.scanning = true;
    this.scanProgressText = 'Scanning receipt image with Gemini Vision AI...';

    this.aiService.scanReceiptImage(this.selectedFile).subscribe({
      next: (response) => {
        this.scanning = false;
        this.handleAiSuccess(response);
      },
      error: (err) => {
        this.scanning = false;
        this.errorMessage = err.error?.message || 'Gemini AI was unable to scan this image. Please ensure the image is clear or try entering text.';
      }
    });
  }

  private scanText(): void {
    this.scanning = true;
    this.scanProgressText = 'Analyzing bill statement text with Gemini AI...';

    this.aiService.scanBillStatement(this.statementText).subscribe({
      next: (response) => {
        this.scanning = false;
        this.handleAiSuccess(response);
      },
      error: (err) => {
        this.scanning = false;
        this.errorMessage = err.error?.message || 'Gemini AI was unable to analyze this statement. Please try again.';
      }
    });
  }

  private handleAiSuccess(response: AiBillScanResponse): void {
    this.aiResponse = response;
    this.scanSuccess = true;

    // Filter categories by the returned type
    this.filterCategoriesByType(response.type);

    // Populate the editable review form
    this.reviewForm.patchValue({
      title: response.title || 'Bill Payment',
      amount: response.amount || '',
      type: response.type || 'EXPENSE',
      categoryId: response.categoryId || (this.filteredCategories.length > 0 ? this.filteredCategories[0].id : ''),
      transactionDate: response.transactionDate || new Date().toISOString().substring(0, 10),
      description: response.description || ''
    });
  }

  // Submit Reviewed & Edited Transaction to Database
  onSubmitTransaction(): void {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched();
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    const formVal = this.reviewForm.value;
    const requestData: TransactionRequest = {
      title: formVal.title.trim(),
      amount: Number(formVal.amount),
      type: formVal.type,
      categoryId: Number(formVal.categoryId),
      transactionDate: formVal.transactionDate,
      description: formVal.description ? formVal.description.trim() : '',
      userId: user.id
    };

    this.transactionService.createTransaction(requestData).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Transaction successfully added to your records!';
        setTimeout(() => {
          this.router.navigate(['/transactions']);
        }, 1200);
      },
      error: (err) => {
        this.saving = false;
        const msg = err.error?.message || '';
        if (msg.includes('User not found')) {
          this.errorMessage = 'Your session has expired. Please log in again to save transactions.';
        } else {
          this.errorMessage = msg || 'Failed to save transaction to database.';
        }
      }
    });
  }

  resetScan(): void {
    this.scanSuccess = false;
    this.aiResponse = null;
    this.clearSelectedFile();
    this.statementText = '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.reviewForm.get(fieldName);
    return !!(field && field.touched && field.invalid);
  }
}
