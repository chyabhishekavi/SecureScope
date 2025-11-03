import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { Finding, FindingStatus } from '../../core/models/finding';
import { FindingService } from '../../core/services/finding.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-finding-details',
  imports: [DatePipe, RouterLink, MaterialModule],
  templateUrl: './finding-details.html',
  styleUrl: './finding-details.scss'
})
export class FindingDetails implements OnInit {
  private readonly findingService = inject(FindingService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly statuses: FindingStatus[] = ['OPEN', 'FIXED', 'IGNORED', 'FALSE_POSITIVE'];
  protected finding: Finding | null = null;
  protected isLoading = false;

  ngOnInit(): void {
    const findingId = this.route.snapshot.paramMap.get('findingId');

    if (!findingId) {
      this.router.navigate(['/findings']);
      return;
    }

    this.loadFinding(findingId);
  }

  protected severityClass(finding: Finding): string {
    return `severity-chip severity-${finding.severity.toLowerCase()}`;
  }

  protected updateStatus(status: FindingStatus): void {
    if (!this.finding) {
      return;
    }

    this.findingService.updateStatus(this.finding.id, status).subscribe({
      next: (finding) => {
        this.finding = finding;
        this.snackBar.open('Finding status updated.', 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Unable to update finding status.', 'Close', { duration: 4000 });
      }
    });
  }

  private loadFinding(findingId: string): void {
    this.isLoading = true;

    this.findingService
      .getFinding(findingId)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (finding) => {
          this.finding = finding;
        },
        error: () => {
          this.snackBar.open('Unable to load finding.', 'Close', { duration: 4000 });
          this.router.navigate(['/findings']);
        }
      });
  }
}
