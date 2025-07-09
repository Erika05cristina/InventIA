import { Routes } from '@angular/router';

export const routes: Routes = [

    {
        path: '',
        redirectTo: 'upload',
        pathMatch: 'full'
    },
    {
        path: 'upload',
        loadComponent: () => import('./components/upload-file').then(m => m.UploadFile)
    }
    
];
