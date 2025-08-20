import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDummy, NewDummy } from '../dummy.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDummy for edit and NewDummyFormGroupInput for create.
 */
type DummyFormGroupInput = IDummy | PartialWithRequiredKeyOf<NewDummy>;

type DummyFormDefaults = Pick<NewDummy, 'id'>;

type DummyFormGroupContent = {
  id: FormControl<IDummy['id'] | NewDummy['id']>;
  name: FormControl<IDummy['name']>;
  description: FormControl<IDummy['description']>;
};

export type DummyFormGroup = FormGroup<DummyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DummyFormService {
  createDummyFormGroup(dummy: DummyFormGroupInput = { id: null }): DummyFormGroup {
    const dummyRawValue = {
      ...this.getFormDefaults(),
      ...dummy,
    };
    return new FormGroup<DummyFormGroupContent>({
      id: new FormControl(
        { value: dummyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(dummyRawValue.name),
      description: new FormControl(dummyRawValue.description),
    });
  }

  getDummy(form: DummyFormGroup): IDummy | NewDummy {
    return form.getRawValue() as IDummy | NewDummy;
  }

  resetForm(form: DummyFormGroup, dummy: DummyFormGroupInput): void {
    const dummyRawValue = { ...this.getFormDefaults(), ...dummy };
    form.reset(
      {
        ...dummyRawValue,
        id: { value: dummyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DummyFormDefaults {
    return {
      id: null,
    };
  }
}
