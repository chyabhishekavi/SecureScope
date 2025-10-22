import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { DashboardMetric, DashboardSummary, ScoreTrendPoint } from '../models/dashboard';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly dashboardUrl = 'http://localhost:8080/api/dashboard';

  constructor(private readonly httpClient: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.httpClient
      .get<ApiResponse<DashboardSummary>>(`${this.dashboardUrl}/summary`)
      .pipe(map((response) => response.data));
  }

  getSeveritySummary(): Observable<DashboardMetric[]> {
    return this.httpClient
      .get<ApiResponse<DashboardMetric[]>>(`${this.dashboardUrl}/severity-summary`)
      .pipe(map((response) => response.data));
  }

  getOwaspSummary(): Observable<DashboardMetric[]> {
    return this.httpClient
      .get<ApiResponse<DashboardMetric[]>>(`${this.dashboardUrl}/owasp-summary`)
      .pipe(map((response) => response.data));
  }

  getScoreTrend(): Observable<ScoreTrendPoint[]> {
    return this.httpClient
      .get<ApiResponse<ScoreTrendPoint[]>>(`${this.dashboardUrl}/score-trend`)
      .pipe(map((response) => response.data));
  }
}
