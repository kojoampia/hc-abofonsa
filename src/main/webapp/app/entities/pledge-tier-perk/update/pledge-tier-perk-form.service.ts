import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPledgeTierPerk, NewPledgeTierPerk } from '../pledge-tier-perk.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPledgeTierPerk for edit and NewPledgeTierPerkFormGroupInput for create.
 */
type PledgeTierPerkFormGroupInput = IPledgeTierPerk | PartialWithRequiredKeyOf<NewPledgeTierPerk>;

type PledgeTierPerkFormDefaults = Pick<NewPledgeTierPerk, 'id'>;

type PledgeTierPerkFormGroupContent = {
  id: FormControl<IPledgeTierPerk['id'] | NewPledgeTierPerk['id']>;
  label: FormControl<IPledgeTierPerk['label']>;
  displayOrder: FormControl<IPledgeTierPerk['displayOrder']>;
  tier: FormControl<IPledgeTierPerk['tier']>;
};

export type PledgeTierPerkFormGroup = FormGroup<PledgeTierPerkFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PledgeTierPerkFormService {
  createPledgeTierPerkFormGroup(pledgeTierPerk?: PledgeTierPerkFormGroupInput): PledgeTierPerkFormGroup {
    const pledgeTierPerkRawValue = {
      ...this.getFormDefaults(),
      ...(pledgeTierPerk ?? { id: null }),
    };
    return new FormGroup<PledgeTierPerkFormGroupContent>({
      id: new FormControl(
        { value: pledgeTierPerkRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(pledgeTierPerkRawValue.label, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      displayOrder: new FormControl(pledgeTierPerkRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tier: new FormControl(pledgeTierPerkRawValue.tier, {
        validators: [Validators.required],
      }),
    });
  }

  getPledgeTierPerk(form: PledgeTierPerkFormGroup): IPledgeTierPerk | NewPledgeTierPerk {
    return form.getRawValue();
  }

  resetForm(form: PledgeTierPerkFormGroup, pledgeTierPerk: PledgeTierPerkFormGroupInput): void {
    const pledgeTierPerkRawValue = { ...this.getFormDefaults(), ...pledgeTierPerk };
    form.reset({
      ...pledgeTierPerkRawValue,
      id: { value: pledgeTierPerkRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PledgeTierPerkFormDefaults {
    return {
      id: null,
    };
  }
}
