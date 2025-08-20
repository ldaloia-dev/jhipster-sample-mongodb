import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'jhipsterSampleMongodbApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'foo',
    data: { pageTitle: 'jhipsterSampleMongodbApp.foo.home.title' },
    loadChildren: () => import('./foo/foo.routes'),
  },
  {
    path: 'dummy',
    data: { pageTitle: 'jhipsterSampleMongodbApp.dummy.home.title' },
    loadChildren: () => import('./dummy/dummy.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
