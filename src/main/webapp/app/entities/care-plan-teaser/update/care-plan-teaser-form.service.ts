import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICarePlanTeaser, NewCarePlanTeaser } from '../care-plan-teaser.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICarePlanTeaser for edit and NewCarePlanTeaserFormGroupInput for create.
 */
type CarePlanTeaserFormGroupInput = ICarePlanTeaser | PartialWithRequiredKeyOf<NewCarePlanTeaser>;

type CarePlanTeaserFormDefaults = Pick<NewCarePlanTeaser, 'id' | 'featured' | 'published'>;

type CarePlanTeaserFormGroupContent = {
  id: FormControl<ICarePlanTeaser['id'] | NewCarePlanTeaser['id']>;
  code: FormControl<ICarePlanTeaser['code']>;
  name: FormControl<ICarePlanTeaser['name']>;
  forWho: FormControl<ICarePlanTeaser['forWho']>;
  priceAmount: FormControl<ICarePlanTeaser['priceAmount']>;
  priceCurrency: FormControl<ICarePlanTeaser['priceCurrency']>;
  pricePeriod: FormControl<ICarePlanTeaser['pricePeriod']>;
  priceNote: FormControl<ICarePlanTeaser['priceNote']>;
  featured: FormControl<ICarePlanTeaser['featured']>;
  displayOrder: FormControl<ICarePlanTeaser['displayOrder']>;
  published: FormControl<ICarePlanTeaser['published']>;
};

export type CarePlanTeaserFormGroup = FormGroup<CarePlanTeaserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CarePlanTeaserFormService {
  createCarePlanTeaserFormGroup(carePlanTeaser?: CarePlanTeaserFormGroupInput): CarePlanTeaserFormGroup {
    const carePlanTeaserRawValue = {
      ...this.getFormDefaults(),
      ...(carePlanTeaser ?? { id: null }),
    };
    return new FormGroup<CarePlanTeaserFormGroupContent>({
      id: new FormControl(
        { value: carePlanTeaserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(carePlanTeaserRawValue.code, {
        validators: [Validators.required],
      }),
      name: new FormControl(carePlanTeaserRawValue.name, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      forWho: new FormControl(carePlanTeaserRawValue.forWho, {
        validators: [Validators.required],
      }),
      priceAmount: new FormControl(carePlanTeaserRawValue.priceAmount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      priceCurrency: new FormControl(carePlanTeaserRawValue.priceCurrency, {
        validators: [Validators.required, Validators.maxLength(3)],
      }),
      pricePeriod: new FormControl(carePlanTeaserRawValue.pricePeriod, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      priceNote: new FormControl(carePlanTeaserRawValue.priceNote, {
        validators: [Validators.maxLength(255)],
      }),
      featured: new FormControl(carePlanTeaserRawValue.featured, {
        validators: [Validators.required],
      }),
      displayOrder: new FormControl(carePlanTeaserRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      published: new FormControl(carePlanTeaserRawValue.published, {
        validators: [Validators.required],
      }),
    });
  }

  getCarePlanTeaser(form: CarePlanTeaserFormGroup): ICarePlanTeaser | NewCarePlanTeaser {
    return form.getRawValue();
  }

  resetForm(form: CarePlanTeaserFormGroup, carePlanTeaser: CarePlanTeaserFormGroupInput): void {
    const carePlanTeaserRawValue = { ...this.getFormDefaults(), ...carePlanTeaser };
    form.reset({
      ...carePlanTeaserRawValue,
      id: { value: carePlanTeaserRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CarePlanTeaserFormDefaults {
    return {
      id: null,
      featured: false,
      published: false,
    };
  }
}
