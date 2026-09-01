import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { BackofficeComponent } from './features/backoffice/backoffice.component';
import { ClientTableComponent } from './features/backoffice/clients/pages/client-table/client-table.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'backoffice',
    component: BackofficeComponent,
    canActivate: [authGuard],
    children: [
      {
      path: 'clientes',
      component: ClientTableComponent,
      outlet: 'main',
    },
      {
      path: 'usuarios',
      component: ClientTableComponent,
      outlet: 'main',
    },
    ]
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
