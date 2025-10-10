import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MaterialModule } from '../../shared/material/material.module';

interface NavigationItem {
  exact: boolean;
  icon: string;
  label: string;
  path: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, MaterialModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {
  protected readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly navigationItems: NavigationItem[] = [
    { exact: true, icon: 'home', label: 'Home', path: '/' },
    { exact: false, icon: 'dashboard', label: 'Dashboard', path: '/dashboard' },
    { exact: false, icon: 'bolt', label: 'Quick Scan', path: '/quick-scan' },
    { exact: false, icon: 'folder_open', label: 'Projects', path: '/projects' },
    { exact: false, icon: 'bug_report', label: 'Findings', path: '/findings' },
    { exact: false, icon: 'description', label: 'Reports', path: '/reports' }
  ];

  protected logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
