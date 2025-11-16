import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { Project } from '../../core/models/project';
import { ProjectScanSummary } from '../../core/models/project-scan-summary';
import { ScanResult } from '../../core/models/scan-result';
import { ZipUploadResponse } from '../../core/models/zip-upload-response';
import { ProjectService } from '../../core/services/project.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-project-details',
  imports: [DatePipe, RouterLink, MaterialModule],
  templateUrl: './project-details.html',
  styleUrl: './project-details.scss'
})
export class ProjectDetails implements OnInit {
  private readonly projectService = inject(ProjectService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly displayedColumns = ['scanName', 'status', 'score', 'risk', 'findings', 'completedAt'];
  protected project: Project | null = null;
  protected isLoading = false;
  protected selectedZipFile: File | null = null;
  protected uploadProgress = 0;
  protected isUploading = false;
  protected isScanning = false;
  protected uploadResponse: ZipUploadResponse | null = null;
  protected scanResult: ScanResult | null = null;

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('projectId');

    if (!projectId) {
      this.router.navigate(['/projects']);
      return;
    }

    this.loadProject(projectId);
  }

  protected deleteProject(): void {
    if (!this.project) {
      return;
    }

    const confirmed = window.confirm(`Delete project "${this.project.name}"? This also removes its linked scan history.`);

    if (!confirmed) {
      return;
    }

    this.projectService.deleteProject(this.project.id).subscribe({
      next: () => {
        this.snackBar.open('Project deleted.', 'Close', { duration: 3000 });
        this.router.navigate(['/projects']);
      },
      error: () => {
        this.snackBar.open('Unable to delete project.', 'Close', { duration: 4000 });
      }
    });
  }

  protected riskClass(scan: ProjectScanSummary): string {
    return `risk-pill risk-${scan.riskLevel.toLowerCase()}`;
  }

  protected scanRiskClass(result: ScanResult): string {
    return `risk-pill risk-${result.riskLevel.toLowerCase()}`;
  }

  protected selectZipFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedZipFile = input.files?.[0] ?? null;
    this.uploadResponse = null;
    this.scanResult = null;
    this.uploadProgress = 0;
  }

  protected uploadZip(): void {
    if (!this.project || !this.selectedZipFile) {
      this.snackBar.open('Choose a ZIP file first.', 'Close', { duration: 3000 });
      return;
    }

    this.isUploading = true;
    this.uploadProgress = 0;
    this.uploadResponse = null;
    this.scanResult = null;

    this.projectService
      .uploadProjectZip(this.project.id, this.selectedZipFile)
      .pipe(finalize(() => (this.isUploading = false)))
      .subscribe({
        next: (event) => {
          if (event.type === 'progress') {
            this.uploadProgress = event.progress;
            return;
          }

          this.uploadProgress = 100;
          this.uploadResponse = event.response;
          this.snackBar.open('ZIP uploaded and validated.', 'Close', { duration: 3000 });
        },
        error: () => {
          this.snackBar.open('Unable to upload ZIP. Confirm it is a valid ZIP under 10 MB.', 'Close', {
            duration: 5000
          });
        }
      });
  }

  protected startZipScan(): void {
    if (!this.project || !this.uploadResponse) {
      this.snackBar.open('Upload a ZIP before starting the scan.', 'Close', { duration: 3000 });
      return;
    }

    this.isScanning = true;
    this.scanResult = null;

    this.projectService
      .startZipScan(this.project.id, this.uploadResponse.uploadId)
      .pipe(finalize(() => (this.isScanning = false)))
      .subscribe({
        next: (scanResult) => {
          this.scanResult = scanResult;
          this.snackBar.open('ZIP scan completed.', 'Close', { duration: 3000 });
          this.loadProject(this.project!.id);
        },
        error: () => {
          this.snackBar.open('Unable to scan uploaded ZIP.', 'Close', { duration: 4000 });
        }
      });
  }

  private loadProject(projectId: string): void {
    this.isLoading = true;

    this.projectService
      .getProject(projectId)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (project) => {
          this.project = project;
        },
        error: () => {
          this.snackBar.open('Unable to load project.', 'Close', { duration: 4000 });
          this.router.navigate(['/projects']);
        }
      });
  }
}
