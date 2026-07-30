import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPledgeTierTeaser, NewPledgeTierTeaser } from '../pledge-tier-teaser.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPledgeTierTeaser for edit and NewPledgeTierTeaserFormGroupInput for create.
 */
type PledgeTierTeaserFormGroupInput = IPledgeTierTeaser | PartialWithRequiredKeyOf<NewPledgeTierTeaser>;

type PledgeTierTeaserFormDefaults = Pick<NewPledgeTierTeaser, 'id' | 'published'>;

type PledgeTierTeaserFormGroupContent = {
  id: FormControl<IPledgeTierTeaser['id'] | NewPledgeTierTeaser['id']>;
  code: FormControl<IPledgeTierTeaser['code']>;
  name: FormControl<IPledgeTierTeaser['name']>;
  blurb: FormControl<IPledgeTierTeaser['blurb']>;
  amount: FormControl<IPledgeTierTeaser['amount']>;
  currency: FormControl<IPledgeTierTeaser['currency']>;
  voucherValue: FormControl<IPledgeTierTeaser['voucherValue']>;
  handoffUrl: FormControl<IPledgeTierTeaser['handoffUrl']>;
  displayOrder: FormControl<IPledgeTierTeaser['displayOrder']>;
  published: FormControl<IPledgeTierTeaser['published']>;
};

export type PledgeTierTeaserFormGroup = FormGroup<PledgeTierTeaserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PledgeTierTeaserFormService {
  createPledgeTierTeaserFormGroup(pledgeTierTeaser?: PledgeTierTeaserFormGroupInput): PledgeTierTeaserFormGroup {
    const pledgeTierTeaserRawValue = {
      ...this.getFormDefaults(),
      ...(pledgeTierTeaser ?? { id: null }),
    };
    return new FormGroup<PledgeTierTeaserFormGroupContent>({
      id: new FormControl(
        { value: pledgeTierTeaserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(pledgeTierTeaserRawValue.code, {
        validators: [Validators.required],
      }),
      name: new FormControl(pledgeTierTeaserRawValue.name, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      blurb: new FormControl(pledgeTierTeaserRawValue.blurb),
      amount: new FormControl(pledgeTierTeaserRawValue.amount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      currency: new FormControl(pledgeTierTeaserRawValue.currency, {
        validators: [Validators.required, Validators.maxLength(3)],
      }),
      voucherValue: new FormControl(pledgeTierTeaserRawValue.voucherValue, {
        validators: [Validators.min(0)],
      }),
      handoffUrl: new FormControl(pledgeTierTeaserRawValue.handoffUrl, {
        validators: [Validators.required, Validators.maxLength(512)],
      }),
      displayOrder: new FormControl(pledgeTierTeaserRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      published: new FormControl(pledgeTierTeaserRawValue.published, {
        validators: [Validators.required],
      }),
    });
  }

  getPledgeTierTeaser(form: PledgeTierTeaserFormGroup): IPledgeTierTeaser | NewPledgeTierTeaser {
    return form.getRawValue();
  }

  resetForm(form: PledgeTierTeaserFormGroup, pledgeTierTeaser: PledgeTierTeaserFormGroupInput): void {
    const pledgeTierTeaserRawValue = { ...this.getFormDefaults(), ...pledgeTierTeaser };
    form.reset({
      ...pledgeTierTeaserRawValue,
      id: { value: pledgeTierTeaserRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PledgeTierTeaserFormDefaults {
    return {
      id: null,
      published: false,
    };
  }
}
