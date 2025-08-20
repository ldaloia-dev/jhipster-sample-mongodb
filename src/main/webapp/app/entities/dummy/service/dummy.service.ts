import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDummy, NewDummy } from '../dummy.model';

export type PartialUpdateDummy = Partial<IDummy> & Pick<IDummy, 'id'>;

export type EntityResponseType = HttpResponse<IDummy>;
export type EntityArrayResponseType = HttpResponse<IDummy[]>;

@Injectable({ providedIn: 'root' })
export class DummyService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dummies');

  create(dummy: NewDummy): Observable<EntityResponseType> {
    return this.http.post<IDummy>(this.resourceUrl, dummy, { observe: 'response' });
  }

  update(dummy: IDummy): Observable<EntityResponseType> {
    return this.http.put<IDummy>(`${this.resourceUrl}/${this.getDummyIdentifier(dummy)}`, dummy, { observe: 'response' });
  }

  partialUpdate(dummy: PartialUpdateDummy): Observable<EntityResponseType> {
    return this.http.patch<IDummy>(`${this.resourceUrl}/${this.getDummyIdentifier(dummy)}`, dummy, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IDummy>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDummy[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDummyIdentifier(dummy: Pick<IDummy, 'id'>): string {
    return dummy.id;
  }

  compareDummy(o1: Pick<IDummy, 'id'> | null, o2: Pick<IDummy, 'id'> | null): boolean {
    return o1 && o2 ? this.getDummyIdentifier(o1) === this.getDummyIdentifier(o2) : o1 === o2;
  }

  addDummyToCollectionIfMissing<Type extends Pick<IDummy, 'id'>>(
    dummyCollection: Type[],
    ...dummiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dummies: Type[] = dummiesToCheck.filter(isPresent);
    if (dummies.length > 0) {
      const dummyCollectionIdentifiers = dummyCollection.map(dummyItem => this.getDummyIdentifier(dummyItem));
      const dummiesToAdd = dummies.filter(dummyItem => {
        const dummyIdentifier = this.getDummyIdentifier(dummyItem);
        if (dummyCollectionIdentifiers.includes(dummyIdentifier)) {
          return false;
        }
        dummyCollectionIdentifiers.push(dummyIdentifier);
        return true;
      });
      return [...dummiesToAdd, ...dummyCollection];
    }
    return dummyCollection;
  }
}
