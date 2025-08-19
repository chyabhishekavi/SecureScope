import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { QuickScanRequest } from '../models/quick-scan-request';
import { ScanResult } from '../models/scan-result';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ScanService {
  private readonly quickScanUrl = 'http://localhost:8080/api/scans/quick-code';

  constructor(private readonly httpClient: HttpClient) {}

  runQuickCodeScan(request: QuickScanRequest): Observable<ScanResult> {
    return this.httpClient
      .post<ApiResponse<ScanResult>>(this.quickScanUrl, request)
      .pipe(map((response) => response.data));
  }
}
