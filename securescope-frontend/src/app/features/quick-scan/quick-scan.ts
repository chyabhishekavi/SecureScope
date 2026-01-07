import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { ScanResult } from '../../core/models/scan-result';
import { ScanService } from '../../core/services/scan.service';
import { EmptyState } from '../../shared/components/empty-state/empty-state';
import { SecurityScoreBadge } from '../../shared/components/security-score-badge/security-score-badge';
import { SeverityChip } from '../../shared/components/severity-chip/severity-chip';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-quick-scan',
  imports: [ReactiveFormsModule, EmptyState, SecurityScoreBadge, SeverityChip, MaterialModule],
  templateUrl: './quick-scan.html',
  styleUrl: './quick-scan.scss'
})
export class QuickScan {
  private readonly formBuilder = inject(FormBuilder);
  private readonly scanService = inject(ScanService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly languages = ['Java', 'JavaScript', 'TypeScript', 'Python', 'PHP', 'C#', 'Other'];
  protected readonly displayedColumns: string[] = [
    'severity',
    'title',
    'owaspCategory',
    'filePath',
    'recommendation'
  ];
  protected isLoading = false;
  protected scanResult: ScanResult | null = null;
  protected errorMessage = '';

  protected readonly quickScanForm = this.formBuilder.nonNullable.group({
    snippetName: ['Auth sample', [Validators.required, Validators.maxLength(80)]],
    language: ['JavaScript', Validators.required],
    fileName: ['auth.js', [Validators.required, Validators.maxLength(120)]],
    codeContent: [
      'const password = "super-secret-password";\nMessageDigest.getInstance("MD5");',
      Validators.required
    ]
  });

  protected runScan(): void {
    if (this.quickScanForm.invalid) {
      this.quickScanForm.markAllAsTouched();
      this.snackBar.open('Please complete the required scan fields.', 'Close', {
        duration: 3500
      });
      return;
    }

    this.isLoading = true;
    this.scanResult = null;
    this.errorMessage = '';

    this.scanService
      .runQuickCodeScan(this.quickScanForm.getRawValue())
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (result) => {
          this.scanResult = result;
          this.snackBar.open('Quick code scan completed.', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
        },
        error: () => {
          this.errorMessage = 'Unable to run scan. Confirm the backend is running on port 8080 and your session is active.';
          this.snackBar.open('Unable to run scan. Confirm the backend is running on port 8080.', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
  }
}
