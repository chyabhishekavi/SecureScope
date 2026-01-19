import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  const authResponse = {
    token: 'jwt-token',
    tokenType: 'Bearer' as const,
    user: {
      id: 'user-id',
      name: 'SecureScope Developer',
      email: 'developer@example.com'
    }
  };

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('stores token and user after login', () => {
    service.login({ email: 'developer@example.com', password: 'password123' }).subscribe((response) => {
      expect(response).toEqual(authResponse);
    });

    const request = httpTestingController.expectOne('http://localhost:8080/api/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush({
      success: true,
      message: 'Login successful',
      data: authResponse,
      timestamp: new Date().toISOString()
    });

    expect(service.getToken()).toBe('jwt-token');
    expect(service.currentUser()).toEqual(authResponse.user);
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('clears stored session on logout', () => {
    service.login({ email: 'developer@example.com', password: 'password123' }).subscribe();
    httpTestingController.expectOne('http://localhost:8080/api/auth/login').flush({
      success: true,
      message: 'Login successful',
      data: authResponse,
      timestamp: new Date().toISOString()
    });

    service.logout();

    expect(service.getToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});
