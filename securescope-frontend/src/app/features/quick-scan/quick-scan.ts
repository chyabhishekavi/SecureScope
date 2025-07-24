import { Component } from '@angular/core';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-quick-scan',
  imports: [MaterialModule],
  templateUrl: './quick-scan.html',
  styleUrl: './quick-scan.scss'
})
export class QuickScan {}
