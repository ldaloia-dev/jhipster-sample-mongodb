import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDummy } from '../dummy.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dummy.test-samples';

import { DummyService } from './dummy.service';

const requireRestSample: IDummy = {
  ...sampleWithRequiredData,
};

describe('Dummy Service', () => {
  let service: DummyService;
  let httpMock: HttpTestingController;
  let expectedResult: IDummy | IDummy[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DummyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Dummy', () => {
      const dummy = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dummy).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dummy', () => {
      const dummy = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dummy).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Dummy', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dummy', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Dummy', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDummyToCollectionIfMissing', () => {
      it('should add a Dummy to an empty array', () => {
        const dummy: IDummy = sampleWithRequiredData;
        expectedResult = service.addDummyToCollectionIfMissing([], dummy);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dummy);
      });

      it('should not add a Dummy to an array that contains it', () => {
        const dummy: IDummy = sampleWithRequiredData;
        const dummyCollection: IDummy[] = [
          {
            ...dummy,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDummyToCollectionIfMissing(dummyCollection, dummy);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dummy to an array that doesn't contain it", () => {
        const dummy: IDummy = sampleWithRequiredData;
        const dummyCollection: IDummy[] = [sampleWithPartialData];
        expectedResult = service.addDummyToCollectionIfMissing(dummyCollection, dummy);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dummy);
      });

      it('should add only unique Dummy to an array', () => {
        const dummyArray: IDummy[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dummyCollection: IDummy[] = [sampleWithRequiredData];
        expectedResult = service.addDummyToCollectionIfMissing(dummyCollection, ...dummyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dummy: IDummy = sampleWithRequiredData;
        const dummy2: IDummy = sampleWithPartialData;
        expectedResult = service.addDummyToCollectionIfMissing([], dummy, dummy2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dummy);
        expect(expectedResult).toContain(dummy2);
      });

      it('should accept null and undefined values', () => {
        const dummy: IDummy = sampleWithRequiredData;
        expectedResult = service.addDummyToCollectionIfMissing([], null, dummy, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dummy);
      });

      it('should return initial array if no Dummy is added', () => {
        const dummyCollection: IDummy[] = [sampleWithRequiredData];
        expectedResult = service.addDummyToCollectionIfMissing(dummyCollection, undefined, null);
        expect(expectedResult).toEqual(dummyCollection);
      });
    });

    describe('compareDummy', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDummy(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
        const entity2 = null;

        const compareResult1 = service.compareDummy(entity1, entity2);
        const compareResult2 = service.compareDummy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
        const entity2 = { id: 'dc269bc9-970b-43d6-ba62-7ea3fb2f05a2' };

        const compareResult1 = service.compareDummy(entity1, entity2);
        const compareResult2 = service.compareDummy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
        const entity2 = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };

        const compareResult1 = service.compareDummy(entity1, entity2);
        const compareResult2 = service.compareDummy(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
