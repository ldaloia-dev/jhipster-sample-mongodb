import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import FooResolve from './route/foo-routing-resolve.service';

const fooRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/foo.component').then(m => m.FooComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/foo-detail.component').then(m => m.FooDetailComponent),
    resolve: {
      foo: FooResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/foo-update.component').then(m => m.FooUpdateComponent),
    resolve: {
      foo: FooResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/foo-update.component').then(m => m.FooUpdateComponent),
    resolve: {
      foo: FooResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default fooRoute;
