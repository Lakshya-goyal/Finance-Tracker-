import { TransactionType } from './transaction.model';

export interface AiBillScanResponse {
  title: string;
  amount: number;
  type: TransactionType;
  categoryId: number | null;
  categoryName: string;
  transactionDate: string;
  description: string;
  rawSummary: string;
}

export interface AiTextScanRequest {
  statement: string;
}
