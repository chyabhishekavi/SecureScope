import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import {
  Finding,
  FindingCategory,
  FindingSeverity,
  FindingStatus
} from '../../core/models/finding';
import { FindingService } from '../../core/services/finding.service';
import { EmptyState } from '../../shared/components/empty-state/empty-state';
import { SeverityChip } from '../../shared/components/severity-chip/severity-chip';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-findings',
  imports: [ReactiveFormsModule, RouterLink, EmptyState, SeverityChip, MaterialModule],
  templateUrl: './findings.html',
  styleUrl: './findings.scss'
})
export class Findings implements OnInit {
  private readonly findingService = inject(FindingService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly severities: FindingSeverity[] = ['INFO', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  protected readonly statuses: FindingStatus[] = ['OPEN', 'FIXED', 'IGNORED', 'FALSE_POSITIVE'];
  protected readonly categories: FindingCategory[] = [
    'HARDCODED_SECRET',
    'VULNERABLE_DEPENDENCY',
    'RISKY_CODE_PATTERN',
    'MISSING_SECURITY_BEST_PRACTICE',
    'OWASP_TOP_TEN',
    'CONFIGURATION_RISK'
  ];
  protected readonly displayedColumns = [
    'severity',
    'title',
    'category',
    'owaspCategory',
    'status',
    'evidence',
    'actions'
  ];

  protected findings: Finding[] = [];
  protected isLoading = false;

  protected readonly filterForm = this.formBuilder.nonNullable.group({
    severity: ['' as FindingSeverity | ''],
    category: ['' as FindingCategory | ''],
    owaspCategory: [''],
    status: ['' as FindingStatus | '']
  });

  ngOnInit(): void {
    this.loadFindings();
  }

  protected loadFindings(): void {
    this.isLoading = true;

    this.findingService
      .getFindings(this.filterForm.getRawValue())
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (findings) => {
          this.findings = findings;
        },
        error: () => {
          this.snackBar.open('Unable to load findings.', 'Close', {
            duration: 4000,
            panelClass: ['error-snackbar']
          });
        }
      });
  }

  protected resetFilters(): void {
    this.filterForm.reset({
      severity: '',
      category: '',
      owaspCategory: '',
      status: ''
    });
    this.loadFindings();
  }

  protected showDependencyFindings(): void {
    this.filterForm.patchValue({
      category: 'VULNERABLE_DEPENDENCY'
    });
    this.loadFindings();
  }

  protected viewFinding(finding: Finding): void {
    this.router.navigate(['/findings', finding.id]);
  }

  protected updateStatus(finding: Finding, status: FindingStatus): void {
    this.findingService.updateStatus(finding.id, status).subscribe({
      next: (updatedFinding) => {
        this.findings = this.findings.map((item) =>
          item.id === updatedFinding.id ? updatedFinding : item
        );
        this.snackBar.open('Finding status updated.', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: () => {
        this.snackBar.open('Unable to update finding status.', 'Close', {
          duration: 4000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  protected categoryClass(finding: Finding): string {
    return finding.category === 'VULNERABLE_DEPENDENCY' ? 'category-chip dependency-chip' : 'category-chip';
  }
}
