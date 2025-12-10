import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ReportPreview, SecurityReport } from '../models/report';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private readonly httpClient: HttpClient) {}

  generateReport(scanId: string): Observable<ReportPreview> {
    return this.httpClient
      .post<ApiResponse<ReportPreview>>(`${this.apiUrl}/scans/${scanId}/reports`, {})
      .pipe(map((response) => response.data));
  }

  getScanReports(scanId: string): Observable<SecurityReport[]> {
    return this.httpClient
      .get<ApiResponse<SecurityReport[]>>(`${this.apiUrl}/scans/${scanId}/reports`)
      .pipe(map((response) => response.data));
  }

  getReport(reportId: string): Observable<ReportPreview> {
    return this.httpClient
      .get<ApiResponse<ReportPreview>>(`${this.apiUrl}/reports/${reportId}`)
      .pipe(map((response) => response.data));
  }

  downloadReport(reportId: string): Observable<Blob> {
    return this.httpClient.get(`${this.apiUrl}/reports/${reportId}/download`, {
      responseType: 'blob'
    });
  }
}
