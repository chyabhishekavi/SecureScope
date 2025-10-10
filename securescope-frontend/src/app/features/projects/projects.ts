import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { Project } from '../../core/models/project';
import { ProjectService } from '../../core/services/project.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-projects',
  imports: [RouterLink, MaterialModule],
  templateUrl: './projects.html',
  styleUrl: './projects.scss'
})
export class Projects implements OnInit {
  private readonly projectService = inject(ProjectService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  protected readonly displayedColumns = ['name', 'sourceType', 'technology', 'scans', 'actions'];
  protected projects: Project[] = [];
  protected isLoading = false;

  ngOnInit(): void {
    this.loadProjects();
  }

  protected loadProjects(): void {
    this.isLoading = true;

    this.projectService
      .getProjects()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (projects) => {
          this.projects = projects;
        },
        error: () => {
          this.snackBar.open('Unable to load projects.', 'Close', { duration: 4000 });
        }
      });
  }

  protected viewProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  protected deleteProject(project: Project): void {
    const confirmed = window.confirm(`Delete project "${project.name}"? This also removes its linked scan history.`);

    if (!confirmed) {
      return;
    }

    this.projectService.deleteProject(project.id).subscribe({
      next: () => {
        this.projects = this.projects.filter((item) => item.id !== project.id);
        this.snackBar.open('Project deleted.', 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Unable to delete project.', 'Close', { duration: 4000 });
      }
    });
  }
}
