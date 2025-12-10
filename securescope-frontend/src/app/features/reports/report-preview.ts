import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { ReportPreview as ReportPreviewModel } from '../../core/models/report';
import { ReportService } from '../../core/services/report.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-report-preview',
  imports: [RouterLink, MaterialModule],
  templateUrl: './report-preview.html',
  styleUrl: './report-preview.scss'
})
export class ReportPreview implements OnInit {
  private readonly reportService = inject(ReportService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected preview: ReportPreviewModel | null = null;
  protected isLoading = false;
  protected isDownloading = false;

  ngOnInit(): void {
    const reportId = this.route.snapshot.paramMap.get('reportId');
    if (!reportId) {
      this.router.navigate(['/reports']);
      return;
    }

    this.loadReport(reportId);
  }

  protected downloadReport(): void {
    if (!this.preview) {
      return;
    }

    const report = this.preview.report;
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

  private loadReport(reportId: string): void {
    this.isLoading = true;

    this.reportService
      .getReport(reportId)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (preview) => {
          this.preview = preview;
        },
        error: () => {
          this.snackBar.open('Unable to load report.', 'Close', { duration: 4000 });
          this.router.navigate(['/reports']);
        }
      });
  }
}
