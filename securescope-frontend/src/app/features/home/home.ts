import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

interface ScanCard {
  badge: string;
  icon: string;
  title: string;
  description: string;
  action: string;
  path: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink, MaterialModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  protected readonly scanCards: ScanCard[] = [
    {
      badge: 'Snippet',
      icon: 'code',
      title: 'Quick Code Scan',
      description: 'Paste a snippet and get fast feedback for secrets, risky patterns, and OWASP context.',
      action: 'Start scan',
      path: '/quick-scan'
    },
    {
      badge: 'Archive',
      icon: 'upload_file',
      title: 'Upload ZIP Scan',
      description: 'Submit a packaged project for deeper source review and dependency inspection.',
      action: 'Open projects',
      path: '/projects'
    },
    {
      badge: 'Repository',
      icon: 'hub',
      title: 'GitHub Repository Scan',
      description: 'Connect a repository and review findings from the same SecureScope workspace.',
      action: 'Connect repo',
      path: '/projects'
    }
  ];

  protected readonly proofPoints = [
    { label: 'OWASP mapped', value: 'Top 10' },
    { label: 'Secret-safe', value: 'Masked' },
    { label: 'Report-ready', value: 'HTML' }
  ];
}
