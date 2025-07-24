import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

interface ScanCard {
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
      icon: 'code',
      title: 'Quick Code Scan',
      description: 'Paste a snippet and get fast feedback for secrets, risky patterns, and OWASP context.',
      action: 'Start scan',
      path: '/quick-scan'
    },
    {
      icon: 'upload_file',
      title: 'Upload ZIP Scan',
      description: 'Submit a packaged project for deeper source review and dependency inspection.',
      action: 'Open projects',
      path: '/projects'
    },
    {
      icon: 'hub',
      title: 'GitHub Repository Scan',
      description: 'Connect a repository and review findings from the same SecureScope workspace.',
      action: 'Connect repo',
      path: '/projects'
    }
  ];
}
