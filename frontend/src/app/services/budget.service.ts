import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Budget, BudgetRequest } from '../models/budget.model';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = 'http://localhost:8080/api/budgets';

  constructor(private http: HttpClient) {}

  getBudgetsByUserId(userId: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${this.apiUrl}/user/${userId}`);
  }

  createBudget(budget: BudgetRequest): Observable<Budget> {
    return this.http.post<Budget>(this.apiUrl, budget);
  }

  updateBudget(id: number, budget: BudgetRequest): Observable<Budget> {
    return this.http.put<Budget>(`${this.apiUrl}/${id}`, budget);
  }

  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
