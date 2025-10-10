import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Project } from '../models/project';
import { ProjectRequest } from '../models/project-request';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

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
}
