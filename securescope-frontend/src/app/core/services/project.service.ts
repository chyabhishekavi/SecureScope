import { HttpClient, HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { filter, map, Observable } from 'rxjs';
import { Project } from '../models/project';
import { ProjectRequest } from '../models/project-request';
import { ScanResult } from '../models/scan-result';
import { ZipUploadResponse } from '../models/zip-upload-response';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export type ZipUploadEvent =
  | { type: 'progress'; progress: number }
  | { type: 'complete'; response: ZipUploadResponse };

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private readonly projectsUrl = 'http://localhost:8080/api/projects';

  constructor(private readonly httpClient: HttpClient) {}

  createProject(request: ProjectRequest): Observable<Project> {
    return this.httpClient
      .post<ApiResponse<Project>>(this.projectsUrl, request)
      .pipe(map((response) => response.data));
  }

  getProjects(): Observable<Project[]> {
    return this.httpClient
      .get<ApiResponse<Project[]>>(this.projectsUrl)
      .pipe(map((response) => response.data));
  }

  getProject(projectId: string): Observable<Project> {
    return this.httpClient
      .get<ApiResponse<Project>>(`${this.projectsUrl}/${projectId}`)
      .pipe(map((response) => response.data));
  }

  updateProject(projectId: string, request: ProjectRequest): Observable<Project> {
    return this.httpClient
      .put<ApiResponse<Project>>(`${this.projectsUrl}/${projectId}`, request)
      .pipe(map((response) => response.data));
  }

  deleteProject(projectId: string): Observable<void> {
    return this.httpClient
      .delete<ApiResponse<void>>(`${this.projectsUrl}/${projectId}`)
      .pipe(map((response) => response.data));
  }

  uploadProjectZip(projectId: string, file: File): Observable<ZipUploadEvent> {
    const formData = new FormData();
    formData.append('file', file);

    return this.httpClient
      .post<ApiResponse<ZipUploadResponse>>(`${this.projectsUrl}/${projectId}/upload`, formData, {
        observe: 'events',
        reportProgress: true
      })
      .pipe(
        filter((event) => event.type === HttpEventType.UploadProgress || event.type === HttpEventType.Response),
        map((event) => this.toZipUploadEvent(event))
      );
  }

  startZipScan(projectId: string, uploadId: string): Observable<ScanResult> {
    return this.httpClient
      .post<ApiResponse<ScanResult>>(`${this.projectsUrl}/${projectId}/scans`, { uploadId })
      .pipe(map((response) => response.data));
  }

  private toZipUploadEvent(event: HttpEvent<ApiResponse<ZipUploadResponse>>): ZipUploadEvent {
    if (event.type === HttpEventType.UploadProgress) {
      const total = event.total ?? event.loaded;
      const progress = total > 0 ? Math.round((event.loaded / total) * 100) : 0;
      return { type: 'progress', progress };
    }

    const response = event as HttpResponse<ApiResponse<ZipUploadResponse>>;
    return { type: 'complete', response: response.body!.data };
  }
}
