import { Component, Input } from '@angular/core';
import { MaterialModule } from '../material/material.module';

@Component({
  selector: 'app-page-placeholder',
  imports: [MaterialModule],
  template: `
    <section class="page-heading">
      <p>{{ eyebrow }}</p>
      <h1>{{ title }}</h1>
      <span>{{ description }}</span>
    </section>

    <mat-card class="placeholder-card">
      <mat-card-content>
        <mat-icon>{{ icon }}</mat-icon>
        <div>
          <h2>{{ cardTitle }}</h2>
          <p>{{ cardText }}</p>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .page-heading {
      margin-bottom: 22px;
    }

    .page-heading p {
      margin: 0 0 8px;
      color: #0f766e;
      font-size: 0.78rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    .page-heading h1 {
      margin: 0 0 10px;
      color: #0f172a;
      font-size: 2rem;
      font-weight: 800;
    }

    .page-heading span {
      display: block;
      max-width: 760px;
      color: #475569;
      line-height: 1.7;
    }

    .placeholder-card {
      border: 1px solid rgba(15, 23, 42, 0.08);
      border-radius: 8px;
      box-shadow: none;
    }

    mat-card-content {
      display: flex;
      align-items: flex-start;
      gap: 18px;
      padding: 24px;
    }

    mat-icon {
      width: 44px;
      height: 44px;
      color: #0f766e;
      font-size: 44px;
    }

    h2 {
      margin: 0 0 8px;
      color: #0f172a;
      font-size: 1.2rem;
    }

    p {
      margin: 0;
      color: #64748b;
      line-height: 1.6;
    }
  `
})
export class PagePlaceholder {
  @Input({ required: true }) eyebrow = '';
  @Input({ required: true }) title = '';
  @Input({ required: true }) description = '';
  @Input({ required: true }) icon = '';
  @Input({ required: true }) cardTitle = '';
  @Input({ required: true }) cardText = '';
}
