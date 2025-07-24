import { Routes } from '@angular/router';
import { Login } from './features/auth/login';
import { Register } from './features/auth/register';
import { Dashboard } from './features/dashboard/dashboard';
import { Findings } from './features/findings/findings';
import { Home } from './features/home/home';
import { Projects } from './features/projects/projects';
import { QuickScan } from './features/quick-scan/quick-scan';
import { Reports } from './features/reports/reports';
import { MainLayout } from './layout/main-layout/main-layout';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', component: Home, title: 'SecureScope' },
      { path: 'dashboard', component: Dashboard, title: 'Dashboard | SecureScope' },
      { path: 'quick-scan', component: QuickScan, title: 'Quick Scan | SecureScope' },
      { path: 'projects', component: Projects, title: 'Projects | SecureScope' },
      { path: 'findings', component: Findings, title: 'Findings | SecureScope' },
      { path: 'reports', component: Reports, title: 'Reports | SecureScope' }
    ]
  },
  { path: 'login', component: Login, title: 'Login | SecureScope' },
  { path: 'register', component: Register, title: 'Register | SecureScope' },
  { path: '**', redirectTo: '' }
];
