import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { ProjectSourceType } from '../../core/models/project';
import { ProjectRequest } from '../../core/models/project-request';
import { ProjectService } from '../../core/services/project.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-project-form',
  imports: [ReactiveFormsModule, RouterLink, MaterialModule],
  templateUrl: './project-form.html',
  styleUrl: './project-form.scss'
})
export class ProjectForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly sourceTypes: { label: string; value: ProjectSourceType }[] = [
    { label: 'Quick Code', value: 'QUICK_CODE' },
    { label: 'ZIP Upload', value: 'ZIP_UPLOAD' },
    { label: 'GitHub Repository', value: 'GITHUB_REPOSITORY' }
  ];

  protected isEditMode = false;
  protected isLoading = false;
  protected projectId: string | null = null;

  protected readonly projectForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(140)]],
    description: ['', Validators.maxLength(800)],
    sourceType: ['GITHUB_REPOSITORY' as ProjectSourceType, Validators.required],
    technology: ['', Validators.maxLength(120)],
    githubUrl: ['', Validators.maxLength(500)]
  });

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('projectId');
    this.isEditMode = Boolean(this.projectId);

    if (this.projectId) {
      this.loadProject(this.projectId);
    }
  }

  protected save(): void {
    if (this.projectForm.invalid) {
      this.projectForm.markAllAsTouched();
      return;
    }

    const request = this.toRequest();
    this.isLoading = true;

    const saveRequest =
      this.isEditMode && this.projectId
        ? this.projectService.updateProject(this.projectId, request)
        : this.projectService.createProject(request);

    saveRequest.pipe(finalize(() => (this.isLoading = false))).subscribe({
      next: (project) => {
        this.snackBar.open(this.isEditMode ? 'Project updated.' : 'Project created.', 'Close', {
          duration: 3000
        });
        this.router.navigate(['/projects', project.id]);
      },
      error: () => {
        this.snackBar.open('Unable to save project.', 'Close', { duration: 4000 });
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
          this.projectForm.patchValue({
            name: project.name,
            description: project.description ?? '',
            sourceType: project.sourceType,
            technology: project.technology ?? '',
            githubUrl: project.githubUrl ?? ''
          });
        },
        error: () => {
          this.snackBar.open('Unable to load project.', 'Close', { duration: 4000 });
          this.router.navigate(['/projects']);
        }
      });
  }

  private toRequest(): ProjectRequest {
    const value = this.projectForm.getRawValue();

    return {
      name: value.name.trim(),
      description: this.optional(value.description),
      sourceType: value.sourceType,
      technology: this.optional(value.technology),
      githubUrl: this.optional(value.githubUrl)
    };
  }

  private optional(value: string): string | null {
    const cleaned = value.trim();
    return cleaned.length > 0 ? cleaned : null;
  }
}
