import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { AuthResponse } from '../models/auth-response';
import { AuthUser } from '../models/auth-user';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

interface StoredSession {
  token: string;
  user: AuthUser;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly sessionKey = 'securescope.auth.session';
  private readonly currentUserSignal = signal<AuthUser | null>(this.loadStoredSession()?.user ?? null);

  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor(private readonly httpClient: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.httpClient
      .post<ApiResponse<AuthResponse>>(`${this.apiUrl}/register`, request)
      .pipe(
        map((response) => response.data),
        tap((authResponse) => this.storeSession(authResponse))
      );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.httpClient
      .post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, request)
      .pipe(
        map((response) => response.data),
        tap((authResponse) => this.storeSession(authResponse))
      );
  }

  logout(): void {
    localStorage.removeItem(this.sessionKey);
    this.currentUserSignal.set(null);
  }

  getToken(): string | null {
    return this.loadStoredSession()?.token ?? null;
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken() && this.currentUser());
  }

  private storeSession(response: AuthResponse): void {
    const session: StoredSession = {
      token: response.token,
      user: response.user
    };

    localStorage.setItem(this.sessionKey, JSON.stringify(session));
    this.currentUserSignal.set(response.user);
  }

  private loadStoredSession(): StoredSession | null {
    const rawSession = localStorage.getItem(this.sessionKey);

    if (!rawSession) {
      return null;
    }

    try {
      return JSON.parse(rawSession) as StoredSession;
    } catch {
      localStorage.removeItem(this.sessionKey);
      return null;
    }
  }
}
