import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Finding, FindingFilters, FindingStatus } from '../models/finding';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class FindingService {
  private readonly findingsUrl = 'http://localhost:8080/api/findings';

  constructor(private readonly httpClient: HttpClient) {}

  getFindings(filters: FindingFilters = {}): Observable<Finding[]> {
    return this.httpClient
      .get<ApiResponse<Finding[]>>(this.findingsUrl, {
        params: this.toParams(filters)
      })
      .pipe(map((response) => response.data));
  }

  getFinding(findingId: string): Observable<Finding> {
    return this.httpClient
      .get<ApiResponse<Finding>>(`${this.findingsUrl}/${findingId}`)
      .pipe(map((response) => response.data));
  }

  updateStatus(findingId: string, status: FindingStatus): Observable<Finding> {
    return this.httpClient
      .patch<ApiResponse<Finding>>(`${this.findingsUrl}/${findingId}/status`, { status })
      .pipe(map((response) => response.data));
  }

  private toParams(filters: FindingFilters): HttpParams {
    let params = new HttpParams();

    if (filters.severity) {
      params = params.set('severity', filters.severity);
    }
    if (filters.category) {
      params = params.set('category', filters.category);
    }
    if (filters.owaspCategory?.trim()) {
      params = params.set('owaspCategory', filters.owaspCategory.trim());
    }
    if (filters.status) {
      params = params.set('status', filters.status);
    }

    return params;
  }
}
