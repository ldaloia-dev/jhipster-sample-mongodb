import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DummyDetailComponent } from './dummy-detail.component';

describe('Dummy Management Detail Component', () => {
  let comp: DummyDetailComponent;
  let fixture: ComponentFixture<DummyDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DummyDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./dummy-detail.component').then(m => m.DummyDetailComponent),
              resolve: { dummy: () => of({ id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DummyDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DummyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load dummy on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DummyDetailComponent);

      // THEN
      expect(instance.dummy()).toEqual(expect.objectContaining({ id: 'fc60085c-b1c0-443c-b4ec-6b3e03cadd8a' }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
