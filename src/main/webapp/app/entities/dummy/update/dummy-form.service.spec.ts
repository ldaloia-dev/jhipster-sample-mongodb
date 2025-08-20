import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dummy.test-samples';

import { DummyFormService } from './dummy-form.service';

describe('Dummy Form Service', () => {
  let service: DummyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DummyFormService);
  });

  describe('Service methods', () => {
    describe('createDummyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDummyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IDummy should create a new form with FormGroup', () => {
        const formGroup = service.createDummyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getDummy', () => {
      it('should return NewDummy for default Dummy initial value', () => {
        const formGroup = service.createDummyFormGroup(sampleWithNewData);

        const dummy = service.getDummy(formGroup) as any;

        expect(dummy).toMatchObject(sampleWithNewData);
      });

      it('should return NewDummy for empty Dummy initial value', () => {
        const formGroup = service.createDummyFormGroup();

        const dummy = service.getDummy(formGroup) as any;

        expect(dummy).toMatchObject({});
      });

      it('should return IDummy', () => {
        const formGroup = service.createDummyFormGroup(sampleWithRequiredData);

        const dummy = service.getDummy(formGroup) as any;

        expect(dummy).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDummy should not enable id FormControl', () => {
        const formGroup = service.createDummyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDummy should disable id FormControl', () => {
        const formGroup = service.createDummyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
