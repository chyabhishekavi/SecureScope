import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, MaterialModule],
  templateUrl: './register.html',
  styleUrl: './auth.scss'
})
export class Register {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected isLoading = false;

  protected readonly registerForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  protected submit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService
      .register(this.registerForm.getRawValue())
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.snackBar.open('SecureScope account created.', 'Close', { duration: 3000 });
          this.router.navigate(['/dashboard']);
        },
        error: (error: unknown) => {
          this.snackBar.open(this.getRegistrationErrorMessage(error), 'Close', {
            duration: 5000
          });
        }
      });
  }

  private getRegistrationErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return 'Unable to reach the backend. Make sure Spring Boot is running on port 8080.';
      }

      const message = error.error?.message;

      if (typeof message === 'string' && message.trim()) {
        return message;
      }
    }

    return 'Unable to create account. Please check the form and try again.';
  }
}
