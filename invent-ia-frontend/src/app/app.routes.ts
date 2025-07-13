import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./components/login/login').then(m => m.Login),
  },
  {
    path: 'upload',
    loadComponent: () =>
      import('./components/upload/upload-file').then(m => m.UploadFile),
  },
];
