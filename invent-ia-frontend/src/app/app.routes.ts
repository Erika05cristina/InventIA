import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' }, // redirección opcional
  { path: 'home', loadComponent: () => import('./components/home/home').then(m => m.Home) },
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.Login) },
  { path: 'upload', loadComponent: () => import('./components/upload/upload-file').then(m => m.UploadFile) },
  { path: 'explanation', loadComponent: () => import('./components/explanation/explanation').then(m => m.Explanation) },
  { path: 'agent', loadComponent: () => import('./components/agent/agent').then(m => m.Agent) },
];
