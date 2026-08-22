export interface Budget {
  id: number;
  categoryId: number;
  categoryName: string;
  userId: number;
  amount: number;
  month: number;
  year: number;
  spentAmount: number;
  remainingAmount: number;
  progressPercentage: number;
  createdAt?: string;
}

export interface BudgetRequest {
  categoryId: number;
  userId: number;
  amount: number;
  month: number;
  year: number;
}
