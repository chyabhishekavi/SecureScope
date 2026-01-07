import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-severity-chip',
  templateUrl: './severity-chip.html',
  styleUrl: './severity-chip.scss'
})
export class SeverityChip {
  readonly severity = input.required<string>();

  protected readonly chipClass = computed(() => `severity-chip severity-${this.severity().toLowerCase()}`);
}
