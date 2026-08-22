import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoryService } from '../services/category.service';
import { Category, CategoryType } from '../models/category.model';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  categoryForm: FormGroup;
  editingCategoryId: number | null = null;

  loading: boolean = true;
  saving: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService
  ) {
    this.categoryForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9\s&\/\-']+$/)
      ]],
      type: ['EXPENSE', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.categoryForm.get(fieldName);
    return !!(field && field.touched && field.invalid);
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Could not load categories.';
      }
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    const formVal = this.categoryForm.value;
    const requestData = {
      name: formVal.name.trim(),
      type: formVal.type
    };

    if (this.editingCategoryId) {
      this.categoryService.updateCategory(this.editingCategoryId, requestData).subscribe({
        next: () => {
          this.saving = false;
          this.successMessage = 'Category updated successfully!';
          this.cancelEdit();
          this.loadCategories();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update category.';
        }
      });
    } else {
      this.categoryService.createCategory(requestData).subscribe({
        next: () => {
          this.saving = false;
          this.successMessage = 'Category added successfully!';
          this.categoryForm.reset({ type: 'EXPENSE' });
          this.loadCategories();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to add category.';
        }
      });
    }
  }

  onEdit(category: Category): void {
    this.editingCategoryId = category.id;
    this.categoryForm.patchValue({
      name: category.name,
      type: category.type
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingCategoryId = null;
    this.categoryForm.reset({ type: 'EXPENSE' });
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this category? All related transactions will also be affected.')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.successMessage = 'Category deleted successfully.';
          this.loadCategories();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: () => {
          this.errorMessage = 'Failed to delete category.';
        }
      });
    }
  }
}
