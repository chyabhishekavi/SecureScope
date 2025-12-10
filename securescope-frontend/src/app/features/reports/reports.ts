import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { SecurityReport } from '../../core/models/report';
import { ScanResult } from '../../core/models/scan-result';
import { ReportService } from '../../core/services/report.service';
import { ScanService } from '../../core/services/scan.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-reports',
  imports: [DatePipe, ReactiveFormsModule, MaterialModule],
  templateUrl: './reports.html',
  styleUrl: './reports.scss'
})
export class Reports implements OnInit {
  private readonly scanService = inject(ScanService);
  private readonly reportService = inject(ReportService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly displayedColumns = ['title', 'format', 'findingCount', 'generatedAt', 'actions'];
  protected scans: ScanResult[] = [];
  protected reports: SecurityReport[] = [];
  protected isLoading = false;
  protected isGenerating = false;
  protected isDownloading = false;

  protected readonly reportForm = this.formBuilder.nonNullable.group({
    scanId: ['']
  });

  ngOnInit(): void {
    this.loadScans();
  }

  protected loadReports(): void {
    const scanId = this.reportForm.controls.scanId.value;
    if (!scanId) {
      this.reports = [];
      return;
    }

    this.reportService.getScanReports(scanId).subscribe({
      next: (reports) => {
        this.reports = reports;
      },
      error: () => {
        this.snackBar.open('Unable to load reports for this scan.', 'Close', { duration: 4000 });
      }
    });
  }

  protected generateReport(): void {
    const scanId = this.reportForm.controls.scanId.value;
    if (!scanId) {
      this.snackBar.open('Choose a scan first.', 'Close', { duration: 3000 });
      return;
    }

    this.isGenerating = true;

    this.reportService
      .generateReport(scanId)
      .pipe(finalize(() => (this.isGenerating = false)))
      .subscribe({
        next: (preview) => {
          this.snackBar.open('Report generated.', 'Close', { duration: 3000 });
          this.router.navigate(['/reports', preview.report.id]);
        },
        error: () => {
          this.snackBar.open('Unable to generate report.', 'Close', { duration: 4000 });
        }
      });
  }

  protected previewReport(report: SecurityReport): void {
    this.router.navigate(['/reports', report.id]);
  }

  protected downloadReport(report: SecurityReport): void {
    this.isDownloading = true;

    this.reportService
      .downloadReport(report.id)
      .pipe(finalize(() => (this.isDownloading = false)))
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          const anchor = document.createElement('a');
          anchor.href = url;
          anchor.download = `securescope-report-${report.id}.html`;
          anchor.click();
          URL.revokeObjectURL(url);
        },
        error: () => {
          this.snackBar.open('Unable to download report.', 'Close', { duration: 4000 });
        }
      });
  }

  private loadScans(): void {
    this.isLoading = true;

    this.scanService
      .getMyScans()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (scans) => {
          this.scans = scans;
          if (scans.length > 0) {
            this.reportForm.patchValue({ scanId: scans[0].scanId });
            this.loadReports();
          }
        },
        error: () => {
          this.snackBar.open('Unable to load scans.', 'Close', { duration: 4000 });
        }
      });
  }
}
