import { Component } from '@angular/core';
import { PagePlaceholder } from '../../shared/components/page-placeholder';

@Component({
  selector: 'app-projects',
  imports: [PagePlaceholder],
  template: `
    <app-page-placeholder
      eyebrow="Projects"
      title="ZIP and repository scans"
      description="Manage uploaded project archives and connected GitHub repositories from one workspace."
      icon="folder_open"
      cardTitle="Project scanning queue"
      cardText="ZIP upload and GitHub connection workflows will be wired to backend scan services in upcoming features."
    />
  `
})
export class Projects {}
