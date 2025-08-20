import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDummy } from 'app/entities/dummy/dummy.model';
import { DummyService } from 'app/entities/dummy/service/dummy.service';
import { IFoo } from '../foo.model';
import { FooService } from '../service/foo.service';
import { FooFormGroup, FooFormService } from './foo-form.service';

@Component({
  selector: 'jhi-foo-update',
  templateUrl: './foo-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FooUpdateComponent implements OnInit {
  isSaving = false;
  foo: IFoo | null = null;

  dummiesSharedCollection: IDummy[] = [];

  protected fooService = inject(FooService);
  protected fooFormService = inject(FooFormService);
  protected dummyService = inject(DummyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FooFormGroup = this.fooFormService.createFooFormGroup();

  compareDummy = (o1: IDummy | null, o2: IDummy | null): boolean => this.dummyService.compareDummy(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ foo }) => {
      this.foo = foo;
      if (foo) {
        this.updateForm(foo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const foo = this.fooFormService.getFoo(this.editForm);
    if (foo.id !== null) {
      this.subscribeToSaveResponse(this.fooService.update(foo));
    } else {
      this.subscribeToSaveResponse(this.fooService.create(foo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFoo>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(foo: IFoo): void {
    this.foo = foo;
    this.fooFormService.resetForm(this.editForm, foo);

    this.dummiesSharedCollection = this.dummyService.addDummyToCollectionIfMissing<IDummy>(this.dummiesSharedCollection, foo.dummy);
  }

  protected loadRelationshipsOptions(): void {
    this.dummyService
      .query()
      .pipe(map((res: HttpResponse<IDummy[]>) => res.body ?? []))
      .pipe(map((dummies: IDummy[]) => this.dummyService.addDummyToCollectionIfMissing<IDummy>(dummies, this.foo?.dummy)))
      .subscribe((dummies: IDummy[]) => (this.dummiesSharedCollection = dummies));
  }
}
