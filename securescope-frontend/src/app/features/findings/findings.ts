import { Component } from '@angular/core';
import { PagePlaceholder } from '../../shared/components/page-placeholder';

@Component({
  selector: 'app-findings',
  imports: [PagePlaceholder],
  template: `
    <app-page-placeholder
      eyebrow="Findings"
      title="Review security findings"
      description="Findings will show severity, OWASP mapping, masked evidence, and developer-friendly remediation notes."
      icon="bug_report"
      cardTitle="Finding list"
      cardText="Filtering, severity badges, and detail views will arrive with the scan result model."
    />
  `
})
export class Findings {}
