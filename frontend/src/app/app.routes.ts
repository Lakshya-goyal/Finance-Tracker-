import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TransactionListComponent } from './transactions/transaction-list/transaction-list.component';
import { TransactionFormComponent } from './transactions/transaction-form/transaction-form.component';
import { CategoryListComponent } from './categories/category-list.component';
import { BudgetListComponent } from './budgets/budget-list.component';
import { AiScannerComponent } from './ai-scanner/ai-scanner.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'transactions', component: TransactionListComponent, canActivate: [authGuard] },
  { path: 'transactions/add', component: TransactionFormComponent, canActivate: [authGuard] },
  { path: 'transactions/edit/:id', component: TransactionFormComponent, canActivate: [authGuard] },
  { path: 'ai-scanner', component: AiScannerComponent, canActivate: [authGuard] },
  { path: 'categories', component: CategoryListComponent, canActivate: [authGuard] },
  { path: 'budgets', component: BudgetListComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];

