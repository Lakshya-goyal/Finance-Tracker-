import { Transaction } from './transaction.model';

export interface CategoryExpense {
  categoryName: string;
  amount: number;
  percentage: number;
}

export interface DashboardData {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  thisMonthExpenses: number;
  recentTransactions: Transaction[];
  categoryExpenses: CategoryExpense[];
}
