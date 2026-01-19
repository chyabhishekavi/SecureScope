import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { ScanService } from '../../core/services/scan.service';
import { QuickScan } from './quick-scan';

describe('QuickScan', () => {
  let fixture: ComponentFixture<QuickScan>;
  let scanService: jasmine.SpyObj<ScanService>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    scanService = jasmine.createSpyObj<ScanService>('ScanService', ['runQuickCodeScan']);
    snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [QuickScan],
      providers: [
        { provide: ScanService, useValue: scanService },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(QuickScan);
  });

  it('marks required fields invalid before running a scan', () => {
    const component = fixture.componentInstance as unknown as {
      quickScanForm: QuickScan['quickScanForm'];
      runScan: () => void;
    };

    component.quickScanForm.patchValue({ snippetName: '', fileName: '', codeContent: '' });

    component.runScan();

    expect(component.quickScanForm.invalid).toBeTrue();
    expect(scanService.runQuickCodeScan).not.toHaveBeenCalled();
  });

  it('submits valid form values to the scan service', () => {
    const scanResult = {
      scanId: 'scan-id',
      status: 'COMPLETED' as const,
      securityScore: 80,
      riskLevel: 'LOW' as const,
      totalFindings: 0,
      findings: []
    };
    scanService.runQuickCodeScan.and.returnValue(of(scanResult));

    const component = fixture.componentInstance as unknown as {
      quickScanForm: QuickScan['quickScanForm'];
      runScan: () => void;
      scanResult: typeof scanResult | null;
    };

    component.quickScanForm.patchValue({
      snippetName: 'Snippet',
      language: 'TypeScript',
      fileName: 'app.ts',
      codeContent: 'const value = process.env.API_KEY;'
    });

    component.runScan();

    expect(scanService.runQuickCodeScan).toHaveBeenCalledWith(component.quickScanForm.getRawValue());
    expect(component.scanResult).toEqual(scanResult);
  });
});
