import { Component } from '@angular/core';
import { PagePlaceholder } from '../../shared/components/page-placeholder';

@Component({
  selector: 'app-dashboard',
  imports: [PagePlaceholder],
  template: `
    <app-page-placeholder
      eyebrow="Dashboard"
      title="Security overview"
      description="This area will summarize scan volume, security scores, severity distribution, and recent findings."
      icon="monitoring"
      cardTitle="Analytics foundation"
      cardText="Charts and scan metrics will be added after the scanning APIs are implemented."
    />
  `
})
export class Dashboard {}
