import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDummy } from '../dummy.model';
import { DummyService } from '../service/dummy.service';

const dummyResolve = (route: ActivatedRouteSnapshot): Observable<null | IDummy> => {
  const id = route.params.id;
  if (id) {
    return inject(DummyService)
      .find(id)
      .pipe(
        mergeMap((dummy: HttpResponse<IDummy>) => {
          if (dummy.body) {
            return of(dummy.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dummyResolve;
