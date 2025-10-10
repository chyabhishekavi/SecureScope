import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { Project } from '../../core/models/project';
import { ProjectScanSummary } from '../../core/models/project-scan-summary';
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
