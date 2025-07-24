import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-header',
  imports: [RouterLink, MaterialModule],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {}
