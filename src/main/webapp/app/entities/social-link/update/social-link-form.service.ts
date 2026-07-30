import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISocialLink, NewSocialLink } from '../social-link.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISocialLink for edit and NewSocialLinkFormGroupInput for create.
 */
type SocialLinkFormGroupInput = ISocialLink | PartialWithRequiredKeyOf<NewSocialLink>;

type SocialLinkFormDefaults = Pick<NewSocialLink, 'id' | 'active'>;

type SocialLinkFormGroupContent = {
  id: FormControl<ISocialLink['id'] | NewSocialLink['id']>;
  platform: FormControl<ISocialLink['platform']>;
  label: FormControl<ISocialLink['label']>;
  url: FormControl<ISocialLink['url']>;
  iconKey: FormControl<ISocialLink['iconKey']>;
  displayOrder: FormControl<ISocialLink['displayOrder']>;
  active: FormControl<ISocialLink['active']>;
};

export type SocialLinkFormGroup = FormGroup<SocialLinkFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SocialLinkFormService {
  createSocialLinkFormGroup(socialLink?: SocialLinkFormGroupInput): SocialLinkFormGroup {
    const socialLinkRawValue = {
      ...this.getFormDefaults(),
      ...(socialLink ?? { id: null }),
    };
    return new FormGroup<SocialLinkFormGroupContent>({
      id: new FormControl(
        { value: socialLinkRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      platform: new FormControl(socialLinkRawValue.platform, {
        validators: [Validators.required],
      }),
      label: new FormControl(socialLinkRawValue.label, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      url: new FormControl(socialLinkRawValue.url, {
        validators: [Validators.required, Validators.maxLength(512)],
      }),
      iconKey: new FormControl(socialLinkRawValue.iconKey, {
        validators: [Validators.maxLength(60)],
      }),
      displayOrder: new FormControl(socialLinkRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      active: new FormControl(socialLinkRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getSocialLink(form: SocialLinkFormGroup): ISocialLink | NewSocialLink {
    return form.getRawValue();
  }

  resetForm(form: SocialLinkFormGroup, socialLink: SocialLinkFormGroupInput): void {
    const socialLinkRawValue = { ...this.getFormDefaults(), ...socialLink };
    form.reset({
      ...socialLinkRawValue,
      id: { value: socialLinkRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SocialLinkFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
