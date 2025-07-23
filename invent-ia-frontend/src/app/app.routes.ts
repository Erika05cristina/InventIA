import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'home', loadComponent: () => import('./components/home/home').then(m => m.Home) },
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.Login) },
  { path: 'upload', loadComponent: () => import('./components/upload/upload-file').then(m => m.UploadFile) },
  { path: 'predict', loadComponent: () => import('./components/predict/predict').then(m => m.Predict) },
  { path: 'explanation', loadComponent: () => import('./components/explanation/explanation').then(m => m.Explanation) },
  { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard').then(m => m.Dashboard) },
  { path: 'agent', loadComponent: () => import('./components/agent/agent').then(m => m.Agent) },
];
