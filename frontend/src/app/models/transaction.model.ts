export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  title: string;
  amount: number;
  type: TransactionType;
  description?: string;
  transactionDate: string;
  categoryId: number;
  categoryName: string;
  userId: number;
  createdAt?: string;
}

export interface TransactionRequest {
  title: string;
  amount: number;
  type: TransactionType;
  description?: string;
  transactionDate: string;
  categoryId: number;
  userId: number;
}
