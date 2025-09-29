import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { FindingResult } from '../models/finding-result';
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
  private readonly scansUrl = 'http://localhost:8080/api/scans';

  constructor(private readonly httpClient: HttpClient) {}

  runQuickCodeScan(request: QuickScanRequest): Observable<ScanResult> {
    return this.httpClient
      .post<ApiResponse<ScanResult>>(`${this.scansUrl}/quick-code`, request)
      .pipe(map((response) => response.data));
  }

  getScan(scanId: string): Observable<ScanResult> {
    return this.httpClient
      .get<ApiResponse<ScanResult>>(`${this.scansUrl}/${scanId}`)
      .pipe(map((response) => response.data));
  }

  getMyScans(): Observable<ScanResult[]> {
    return this.httpClient
      .get<ApiResponse<ScanResult[]>>(`${this.scansUrl}/my-scans`)
      .pipe(map((response) => response.data));
  }

  getScanFindings(scanId: string): Observable<FindingResult[]> {
    return this.httpClient
      .get<ApiResponse<FindingResult[]>>(`${this.scansUrl}/${scanId}/findings`)
      .pipe(map((response) => response.data));
  }
}
