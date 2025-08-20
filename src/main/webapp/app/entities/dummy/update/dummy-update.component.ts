import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDummy } from '../dummy.model';
import { DummyService } from '../service/dummy.service';
import { DummyFormGroup, DummyFormService } from './dummy-form.service';

@Component({
  selector: 'jhi-dummy-update',
  templateUrl: './dummy-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DummyUpdateComponent implements OnInit {
  isSaving = false;
  dummy: IDummy | null = null;

  protected dummyService = inject(DummyService);
  protected dummyFormService = inject(DummyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DummyFormGroup = this.dummyFormService.createDummyFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dummy }) => {
      this.dummy = dummy;
      if (dummy) {
        this.updateForm(dummy);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dummy = this.dummyFormService.getDummy(this.editForm);
    if (dummy.id !== null) {
      this.subscribeToSaveResponse(this.dummyService.update(dummy));
    } else {
      this.subscribeToSaveResponse(this.dummyService.create(dummy));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDummy>>): void {
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

  protected updateForm(dummy: IDummy): void {
    this.dummy = dummy;
    this.dummyFormService.resetForm(this.editForm, dummy);
  }
}
