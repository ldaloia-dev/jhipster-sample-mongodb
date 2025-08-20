import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDummy } from 'app/entities/dummy/dummy.model';
import { DummyService } from 'app/entities/dummy/service/dummy.service';
import { FooService } from '../service/foo.service';
import { IFoo } from '../foo.model';
import { FooFormService } from './foo-form.service';

import { FooUpdateComponent } from './foo-update.component';

describe('Foo Management Update Component', () => {
  let comp: FooUpdateComponent;
  let fixture: ComponentFixture<FooUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fooFormService: FooFormService;
  let fooService: FooService;
  let dummyService: DummyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FooUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FooUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FooUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fooFormService = TestBed.inject(FooFormService);
    fooService = TestBed.inject(FooService);
    dummyService = TestBed.inject(DummyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Dummy query and add missing value', () => {
      const foo: IFoo = { id: '3b7fd2a2-2e08-4346-b7a0-d8c0c0c5722f' };
      const dummy: IDummy = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
      foo.dummy = dummy;

      const dummyCollection: IDummy[] = [{ id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' }];
      jest.spyOn(dummyService, 'query').mockReturnValue(of(new HttpResponse({ body: dummyCollection })));
      const additionalDummies = [dummy];
      const expectedCollection: IDummy[] = [...additionalDummies, ...dummyCollection];
      jest.spyOn(dummyService, 'addDummyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ foo });
      comp.ngOnInit();

      expect(dummyService.query).toHaveBeenCalled();
      expect(dummyService.addDummyToCollectionIfMissing).toHaveBeenCalledWith(
        dummyCollection,
        ...additionalDummies.map(expect.objectContaining),
      );
      expect(comp.dummiesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const foo: IFoo = { id: '3b7fd2a2-2e08-4346-b7a0-d8c0c0c5722f' };
      const dummy: IDummy = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
      foo.dummy = dummy;

      activatedRoute.data = of({ foo });
      comp.ngOnInit();

      expect(comp.dummiesSharedCollection).toContainEqual(dummy);
      expect(comp.foo).toEqual(foo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFoo>>();
      const foo = { id: '5c78ca54-e49a-48c3-a55a-ae0ef64d12fe' };
      jest.spyOn(fooFormService, 'getFoo').mockReturnValue(foo);
      jest.spyOn(fooService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ foo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: foo }));
      saveSubject.complete();

      // THEN
      expect(fooFormService.getFoo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fooService.update).toHaveBeenCalledWith(expect.objectContaining(foo));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFoo>>();
      const foo = { id: '5c78ca54-e49a-48c3-a55a-ae0ef64d12fe' };
      jest.spyOn(fooFormService, 'getFoo').mockReturnValue({ id: null });
      jest.spyOn(fooService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ foo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: foo }));
      saveSubject.complete();

      // THEN
      expect(fooFormService.getFoo).toHaveBeenCalled();
      expect(fooService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFoo>>();
      const foo = { id: '5c78ca54-e49a-48c3-a55a-ae0ef64d12fe' };
      jest.spyOn(fooService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ foo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fooService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDummy', () => {
      it('should forward to dummyService', () => {
        const entity = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
        const entity2 = { id: 'dc269bc9-970b-43d6-ba62-7ea3fb2f05a2' };
        jest.spyOn(dummyService, 'compareDummy');
        comp.compareDummy(entity, entity2);
        expect(dummyService.compareDummy).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
