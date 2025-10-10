import { Routes } from '@angular/router';
import { Login } from './features/auth/login';
import { Register } from './features/auth/register';
import { Dashboard } from './features/dashboard/dashboard';
import { Findings } from './features/findings/findings';
import { Home } from './features/home/home';
import { ProjectDetails } from './features/projects/project-details';
import { ProjectForm } from './features/projects/project-form';
import { Projects } from './features/projects/projects';
import { QuickScan } from './features/quick-scan/quick-scan';
import { Reports } from './features/reports/reports';
import { MainLayout } from './layout/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', component: Home, title: 'SecureScope' },
      { path: 'dashboard', component: Dashboard, title: 'Dashboard | SecureScope', canActivate: [authGuard] },
      { path: 'quick-scan', component: QuickScan, title: 'Quick Scan | SecureScope', canActivate: [authGuard] },
      { path: 'projects', component: Projects, title: 'Projects | SecureScope', canActivate: [authGuard] },
      { path: 'projects/new', component: ProjectForm, title: 'New Project | SecureScope', canActivate: [authGuard] },
      { path: 'projects/:projectId/edit', component: ProjectForm, title: 'Edit Project | SecureScope', canActivate: [authGuard] },
      { path: 'projects/:projectId', component: ProjectDetails, title: 'Project Details | SecureScope', canActivate: [authGuard] },
      { path: 'findings', component: Findings, title: 'Findings | SecureScope', canActivate: [authGuard] },
      { path: 'reports', component: Reports, title: 'Reports | SecureScope', canActivate: [authGuard] }
    ]
  },
  { path: 'login', component: Login, title: 'Login | SecureScope', canActivate: [guestGuard] },
  { path: 'register', component: Register, title: 'Register | SecureScope', canActivate: [guestGuard] },
  { path: '**', redirectTo: '' }
];
