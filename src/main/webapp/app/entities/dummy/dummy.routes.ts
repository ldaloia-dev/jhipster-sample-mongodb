import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import DummyResolve from './route/dummy-routing-resolve.service';

const dummyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dummy.component').then(m => m.DummyComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dummy-detail.component').then(m => m.DummyDetailComponent),
    resolve: {
      dummy: DummyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dummy-update.component').then(m => m.DummyUpdateComponent),
    resolve: {
      dummy: DummyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dummy-update.component').then(m => m.DummyUpdateComponent),
    resolve: {
      dummy: DummyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dummyRoute;
