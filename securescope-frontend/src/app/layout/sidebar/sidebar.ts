import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

interface NavigationItem {
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
  protected readonly navigationItems: NavigationItem[] = [
    { icon: 'home', label: 'Home', path: '/' },
    { icon: 'dashboard', label: 'Dashboard', path: '/dashboard' },
    { icon: 'bolt', label: 'Quick Scan', path: '/quick-scan' },
    { icon: 'folder_open', label: 'Projects', path: '/projects' },
    { icon: 'bug_report', label: 'Findings', path: '/findings' },
    { icon: 'description', label: 'Reports', path: '/reports' }
  ];
}
