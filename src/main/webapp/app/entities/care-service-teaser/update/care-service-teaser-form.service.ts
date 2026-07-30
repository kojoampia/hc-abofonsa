import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICareServiceTeaser, NewCareServiceTeaser } from '../care-service-teaser.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICareServiceTeaser for edit and NewCareServiceTeaserFormGroupInput for create.
 */
type CareServiceTeaserFormGroupInput = ICareServiceTeaser | PartialWithRequiredKeyOf<NewCareServiceTeaser>;

type CareServiceTeaserFormDefaults = Pick<NewCareServiceTeaser, 'id' | 'published'>;

type CareServiceTeaserFormGroupContent = {
  id: FormControl<ICareServiceTeaser['id'] | NewCareServiceTeaser['id']>;
  slug: FormControl<ICareServiceTeaser['slug']>;
  name: FormControl<ICareServiceTeaser['name']>;
  blurb: FormControl<ICareServiceTeaser['blurb']>;
  iconKey: FormControl<ICareServiceTeaser['iconKey']>;
  availableOn: FormControl<ICareServiceTeaser['availableOn']>;
  displayOrder: FormControl<ICareServiceTeaser['displayOrder']>;
  published: FormControl<ICareServiceTeaser['published']>;
};

export type CareServiceTeaserFormGroup = FormGroup<CareServiceTeaserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CareServiceTeaserFormService {
  createCareServiceTeaserFormGroup(careServiceTeaser?: CareServiceTeaserFormGroupInput): CareServiceTeaserFormGroup {
    const careServiceTeaserRawValue = {
      ...this.getFormDefaults(),
      ...(careServiceTeaser ?? { id: null }),
    };
    return new FormGroup<CareServiceTeaserFormGroupContent>({
      id: new FormControl(
        { value: careServiceTeaserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      slug: new FormControl(careServiceTeaserRawValue.slug, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      name: new FormControl(careServiceTeaserRawValue.name, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      blurb: new FormControl(careServiceTeaserRawValue.blurb, {
        validators: [Validators.required],
      }),
      iconKey: new FormControl(careServiceTeaserRawValue.iconKey, {
        validators: [Validators.maxLength(60)],
      }),
      availableOn: new FormControl(careServiceTeaserRawValue.availableOn, {
        validators: [Validators.maxLength(160)],
      }),
      displayOrder: new FormControl(careServiceTeaserRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      published: new FormControl(careServiceTeaserRawValue.published, {
        validators: [Validators.required],
      }),
    });
  }

  getCareServiceTeaser(form: CareServiceTeaserFormGroup): ICareServiceTeaser | NewCareServiceTeaser {
    return form.getRawValue();
  }

  resetForm(form: CareServiceTeaserFormGroup, careServiceTeaser: CareServiceTeaserFormGroupInput): void {
    const careServiceTeaserRawValue = { ...this.getFormDefaults(), ...careServiceTeaser };
    form.reset({
      ...careServiceTeaserRawValue,
      id: { value: careServiceTeaserRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CareServiceTeaserFormDefaults {
    return {
      id: null,
      published: false,
    };
  }
}
