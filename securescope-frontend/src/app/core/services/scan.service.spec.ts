import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ScanService } from './scan.service';

describe('ScanService', () => {
  let service: ScanService;
  let httpTestingController: HttpTestingController;

  const scanResult = {
    scanId: 'scan-id',
    status: 'COMPLETED' as const,
    securityScore: 70,
    riskLevel: 'MODERATE' as const,
    totalFindings: 1,
    findings: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(ScanService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('posts quick code scan requests and returns response data', () => {
    const requestBody = {
      snippetName: 'Auth sample',
      language: 'JavaScript',
      fileName: 'auth.js',
      codeContent: 'const password = "secret";'
    };

    service.runQuickCodeScan(requestBody).subscribe((response) => {
      expect(response).toEqual(scanResult);
    });

    const request = httpTestingController.expectOne('http://localhost:8080/api/scans/quick-code');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);
    request.flush({
      success: true,
      message: 'Quick code scan completed',
      data: scanResult,
      timestamp: new Date().toISOString()
    });
  });

  it('loads scans owned by the current user', () => {
    service.getMyScans().subscribe((response) => {
      expect(response).toEqual([scanResult]);
    });

    const request = httpTestingController.expectOne('http://localhost:8080/api/scans/my-scans');
    expect(request.request.method).toBe('GET');
    request.flush({
      success: true,
      message: 'Scans loaded',
      data: [scanResult],
      timestamp: new Date().toISOString()
    });
  });
});
