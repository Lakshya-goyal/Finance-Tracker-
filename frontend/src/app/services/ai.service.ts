import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AiBillScanResponse, AiTextScanRequest } from '../models/ai-scan.model';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = 'http://localhost:8080/api/ai';

  constructor(private http: HttpClient) {}

  /**
   * Send an image of a receipt/bill to Gemini AI for scanning & categorization
   */
  scanReceiptImage(file: File): Observable<AiBillScanResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AiBillScanResponse>(`${this.apiUrl}/scan-receipt`, formData);
  }

  /**
   * Send a text bill statement or SMS notification to Gemini AI
   */
  scanBillStatement(statement: string): Observable<AiBillScanResponse> {
    const request: AiTextScanRequest = { statement };
    return this.http.post<AiBillScanResponse>(`${this.apiUrl}/scan-statement`, request);
  }
}
