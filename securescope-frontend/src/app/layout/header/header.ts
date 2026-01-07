import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MaterialModule } from '../../shared/material/material.module';

interface NavigationItem {
  icon: string;
  label: string;
  path: string;
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, MaterialModule],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  protected readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly navigationItems: NavigationItem[] = [
    { icon: 'dashboard', label: 'Dashboard', path: '/dashboard' },
    { icon: 'bolt', label: 'Quick Scan', path: '/quick-scan' },
    { icon: 'folder_open', label: 'Projects', path: '/projects' },
    { icon: 'bug_report', label: 'Findings', path: '/findings' },
    { icon: 'description', label: 'Reports', path: '/reports' }
  ];

  protected logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
