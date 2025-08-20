import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFoo, NewFoo } from '../foo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFoo for edit and NewFooFormGroupInput for create.
 */
type FooFormGroupInput = IFoo | PartialWithRequiredKeyOf<NewFoo>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFoo | NewFoo> = Omit<T, 'start'> & {
  start?: string | null;
};

type FooFormRawValue = FormValueOf<IFoo>;

type NewFooFormRawValue = FormValueOf<NewFoo>;

type FooFormDefaults = Pick<NewFoo, 'id' | 'start'>;

type FooFormGroupContent = {
  id: FormControl<FooFormRawValue['id'] | NewFoo['id']>;
  name: FormControl<FooFormRawValue['name']>;
  description: FormControl<FooFormRawValue['description']>;
  start: FormControl<FooFormRawValue['start']>;
  dummy: FormControl<FooFormRawValue['dummy']>;
};

export type FooFormGroup = FormGroup<FooFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FooFormService {
  createFooFormGroup(foo: FooFormGroupInput = { id: null }): FooFormGroup {
    const fooRawValue = this.convertFooToFooRawValue({
      ...this.getFormDefaults(),
      ...foo,
    });
    return new FormGroup<FooFormGroupContent>({
      id: new FormControl(
        { value: fooRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(fooRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(fooRawValue.description),
      start: new FormControl(fooRawValue.start),
      dummy: new FormControl(fooRawValue.dummy),
    });
  }

  getFoo(form: FooFormGroup): IFoo | NewFoo {
    return this.convertFooRawValueToFoo(form.getRawValue() as FooFormRawValue | NewFooFormRawValue);
  }

  resetForm(form: FooFormGroup, foo: FooFormGroupInput): void {
    const fooRawValue = this.convertFooToFooRawValue({ ...this.getFormDefaults(), ...foo });
    form.reset(
      {
        ...fooRawValue,
        id: { value: fooRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FooFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      start: currentTime,
    };
  }

  private convertFooRawValueToFoo(rawFoo: FooFormRawValue | NewFooFormRawValue): IFoo | NewFoo {
    return {
      ...rawFoo,
      start: dayjs(rawFoo.start, DATE_TIME_FORMAT),
    };
  }

  private convertFooToFooRawValue(
    foo: IFoo | (Partial<NewFoo> & FooFormDefaults),
  ): FooFormRawValue | PartialWithRequiredKeyOf<NewFooFormRawValue> {
    return {
      ...foo,
      start: foo.start ? foo.start.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
