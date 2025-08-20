import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DummyService } from '../service/dummy.service';
import { IDummy } from '../dummy.model';
import { DummyFormService } from './dummy-form.service';

import { DummyUpdateComponent } from './dummy-update.component';

describe('Dummy Management Update Component', () => {
  let comp: DummyUpdateComponent;
  let fixture: ComponentFixture<DummyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dummyFormService: DummyFormService;
  let dummyService: DummyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DummyUpdateComponent],
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
      .overrideTemplate(DummyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DummyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dummyFormService = TestBed.inject(DummyFormService);
    dummyService = TestBed.inject(DummyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const dummy: IDummy = { id: 'dc269bc9-970b-43d6-ba62-7ea3fb2f05a2' };

      activatedRoute.data = of({ dummy });
      comp.ngOnInit();

      expect(comp.dummy).toEqual(dummy);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDummy>>();
      const dummy = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
      jest.spyOn(dummyFormService, 'getDummy').mockReturnValue(dummy);
      jest.spyOn(dummyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dummy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dummy }));
      saveSubject.complete();

      // THEN
      expect(dummyFormService.getDummy).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dummyService.update).toHaveBeenCalledWith(expect.objectContaining(dummy));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDummy>>();
      const dummy = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
      jest.spyOn(dummyFormService, 'getDummy').mockReturnValue({ id: null });
      jest.spyOn(dummyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dummy: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dummy }));
      saveSubject.complete();

      // THEN
      expect(dummyFormService.getDummy).toHaveBeenCalled();
      expect(dummyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDummy>>();
      const dummy = { id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' };
      jest.spyOn(dummyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dummy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dummyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
