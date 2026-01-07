import { Component, input } from '@angular/core';
import { MaterialModule } from '../../material/material.module';

@Component({
  selector: 'app-empty-state',
  imports: [MaterialModule],
  templateUrl: './empty-state.html',
  styleUrl: './empty-state.scss'
})
export class EmptyState {
  readonly icon = input('inbox');
  readonly title = input('Nothing to show yet');
  readonly message = input('');
}
