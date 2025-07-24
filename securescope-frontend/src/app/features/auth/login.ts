import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-login',
  imports: [RouterLink, MaterialModule],
  templateUrl: './login.html',
  styleUrl: './auth.scss'
})
export class Login {}
