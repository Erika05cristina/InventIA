import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' }, // redirección opcional
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.Login) },
  { path: 'upload', loadComponent: () => import('./components/upload/upload-file').then(m => m.UploadFile) },
  { path: 'home', loadComponent: () => import('./components/home/home').then(m => m.Home) },
];
