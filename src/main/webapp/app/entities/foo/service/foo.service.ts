import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFoo, NewFoo } from '../foo.model';

export type PartialUpdateFoo = Partial<IFoo> & Pick<IFoo, 'id'>;

type RestOf<T extends IFoo | NewFoo> = Omit<T, 'start'> & {
  start?: string | null;
};

export type RestFoo = RestOf<IFoo>;

export type NewRestFoo = RestOf<NewFoo>;

export type PartialUpdateRestFoo = RestOf<PartialUpdateFoo>;

export type EntityResponseType = HttpResponse<IFoo>;
export type EntityArrayResponseType = HttpResponse<IFoo[]>;

@Injectable({ providedIn: 'root' })
export class FooService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/foos');

  create(foo: NewFoo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(foo);
    return this.http.post<RestFoo>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(foo: IFoo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(foo);
    return this.http
      .put<RestFoo>(`${this.resourceUrl}/${this.getFooIdentifier(foo)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(foo: PartialUpdateFoo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(foo);
    return this.http
      .patch<RestFoo>(`${this.resourceUrl}/${this.getFooIdentifier(foo)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestFoo>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFoo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFooIdentifier(foo: Pick<IFoo, 'id'>): string {
    return foo.id;
  }

  compareFoo(o1: Pick<IFoo, 'id'> | null, o2: Pick<IFoo, 'id'> | null): boolean {
    return o1 && o2 ? this.getFooIdentifier(o1) === this.getFooIdentifier(o2) : o1 === o2;
  }

  addFooToCollectionIfMissing<Type extends Pick<IFoo, 'id'>>(fooCollection: Type[], ...foosToCheck: (Type | null | undefined)[]): Type[] {
    const foos: Type[] = foosToCheck.filter(isPresent);
    if (foos.length > 0) {
      const fooCollectionIdentifiers = fooCollection.map(fooItem => this.getFooIdentifier(fooItem));
      const foosToAdd = foos.filter(fooItem => {
        const fooIdentifier = this.getFooIdentifier(fooItem);
        if (fooCollectionIdentifiers.includes(fooIdentifier)) {
          return false;
        }
        fooCollectionIdentifiers.push(fooIdentifier);
        return true;
      });
      return [...foosToAdd, ...fooCollection];
    }
    return fooCollection;
  }

  protected convertDateFromClient<T extends IFoo | NewFoo | PartialUpdateFoo>(foo: T): RestOf<T> {
    return {
      ...foo,
      start: foo.start?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFoo: RestFoo): IFoo {
    return {
      ...restFoo,
      start: restFoo.start ? dayjs(restFoo.start) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFoo>): HttpResponse<IFoo> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFoo[]>): HttpResponse<IFoo[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
