import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-security-score-badge',
  templateUrl: './security-score-badge.html',
  styleUrl: './security-score-badge.scss'
})
export class SecurityScoreBadge {
  readonly score = input.required<number>();

  protected readonly scoreClass = computed(() => {
    const score = this.score();
    if (score >= 90) {
      return 'score-excellent';
    }
    if (score >= 75) {
      return 'score-good';
    }
    if (score >= 55) {
      return 'score-fair';
    }
    return 'score-poor';
  });
}
