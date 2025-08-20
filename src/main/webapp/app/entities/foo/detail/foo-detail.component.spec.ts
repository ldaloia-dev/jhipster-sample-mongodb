import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FooDetailComponent } from './foo-detail.component';

describe('Foo Management Detail Component', () => {
  let comp: FooDetailComponent;
  let fixture: ComponentFixture<FooDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./foo-detail.component').then(m => m.FooDetailComponent),
              resolve: { foo: () => of({ id: '5c78ca54-e49a-48c3-a55a-ae0ef64d12fe' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FooDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FooDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load foo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FooDetailComponent);

      // THEN
      expect(instance.foo()).toEqual(expect.objectContaining({ id: '5c78ca54-e49a-48c3-a55a-ae0ef64d12fe' }));
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
