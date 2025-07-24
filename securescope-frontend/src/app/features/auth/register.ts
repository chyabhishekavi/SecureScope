import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-register',
  imports: [RouterLink, MaterialModule],
  templateUrl: './register.html',
  styleUrl: './auth.scss'
})
export class Register {}
