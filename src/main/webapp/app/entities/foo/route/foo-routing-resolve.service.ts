import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFoo } from '../foo.model';
import { FooService } from '../service/foo.service';

const fooResolve = (route: ActivatedRouteSnapshot): Observable<null | IFoo> => {
  const id = route.params.id;
  if (id) {
    return inject(FooService)
      .find(id)
      .pipe(
        mergeMap((foo: HttpResponse<IFoo>) => {
          if (foo.body) {
            return of(foo.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default fooResolve;
