import { Component } from '@angular/core';
import { PagePlaceholder } from '../../shared/components/page-placeholder';

@Component({
  selector: 'app-reports',
  imports: [PagePlaceholder],
  template: `
    <app-page-placeholder
      eyebrow="Reports"
      title="Exportable security reports"
      description="HTML and PDF report workflows will summarize scores, findings, OWASP coverage, and remediation guidance."
      icon="description"
      cardTitle="Report center"
      cardText="Report generation will be connected after scan persistence and findings are available."
    />
  `
})
export class Reports {}
