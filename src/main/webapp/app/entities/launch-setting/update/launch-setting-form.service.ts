import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILaunchSetting, NewLaunchSetting } from '../launch-setting.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILaunchSetting for edit and NewLaunchSettingFormGroupInput for create.
 */
type LaunchSettingFormGroupInput = ILaunchSetting | PartialWithRequiredKeyOf<NewLaunchSetting>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILaunchSetting | NewLaunchSetting> = Omit<T, 'launchAt'> & {
  launchAt?: string | null;
};

type LaunchSettingFormRawValue = FormValueOf<ILaunchSetting>;

type NewLaunchSettingFormRawValue = FormValueOf<NewLaunchSetting>;

type LaunchSettingFormDefaults = Pick<NewLaunchSetting, 'id' | 'launchAt' | 'active'>;

type LaunchSettingFormGroupContent = {
  id: FormControl<LaunchSettingFormRawValue['id'] | NewLaunchSetting['id']>;
  settingKey: FormControl<LaunchSettingFormRawValue['settingKey']>;
  organisationName: FormControl<LaunchSettingFormRawValue['organisationName']>;
  tagline: FormControl<LaunchSettingFormRawValue['tagline']>;
  launchAt: FormControl<LaunchSettingFormRawValue['launchAt']>;
  launchTimezone: FormControl<LaunchSettingFormRawValue['launchTimezone']>;
  fundUrl: FormControl<LaunchSettingFormRawValue['fundUrl']>;
  contactEmail: FormControl<LaunchSettingFormRawValue['contactEmail']>;
  contactPhone: FormControl<LaunchSettingFormRawValue['contactPhone']>;
  officeAddress: FormControl<LaunchSettingFormRawValue['officeAddress']>;
  active: FormControl<LaunchSettingFormRawValue['active']>;
};

export type LaunchSettingFormGroup = FormGroup<LaunchSettingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LaunchSettingFormService {
  createLaunchSettingFormGroup(launchSetting?: LaunchSettingFormGroupInput): LaunchSettingFormGroup {
    const launchSettingRawValue = this.convertLaunchSettingToLaunchSettingRawValue({
      ...this.getFormDefaults(),
      ...(launchSetting ?? { id: null }),
    });
    return new FormGroup<LaunchSettingFormGroupContent>({
      id: new FormControl(
        { value: launchSettingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      settingKey: new FormControl(launchSettingRawValue.settingKey, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      organisationName: new FormControl(launchSettingRawValue.organisationName, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      tagline: new FormControl(launchSettingRawValue.tagline, {
        validators: [Validators.maxLength(255)],
      }),
      launchAt: new FormControl(launchSettingRawValue.launchAt, {
        validators: [Validators.required],
      }),
      launchTimezone: new FormControl(launchSettingRawValue.launchTimezone, {
        validators: [Validators.required, Validators.maxLength(64)],
      }),
      fundUrl: new FormControl(launchSettingRawValue.fundUrl, {
        validators: [Validators.required, Validators.maxLength(512)],
      }),
      contactEmail: new FormControl(launchSettingRawValue.contactEmail, {
        validators: [Validators.required, Validators.maxLength(254)],
      }),
      contactPhone: new FormControl(launchSettingRawValue.contactPhone, {
        validators: [Validators.maxLength(40)],
      }),
      officeAddress: new FormControl(launchSettingRawValue.officeAddress, {
        validators: [Validators.maxLength(255)],
      }),
      active: new FormControl(launchSettingRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getLaunchSetting(form: LaunchSettingFormGroup): ILaunchSetting | NewLaunchSetting {
    return this.convertLaunchSettingRawValueToLaunchSetting(form.getRawValue());
  }

  resetForm(form: LaunchSettingFormGroup, launchSetting: LaunchSettingFormGroupInput): void {
    const launchSettingRawValue = this.convertLaunchSettingToLaunchSettingRawValue({ ...this.getFormDefaults(), ...launchSetting });
    form.reset({
      ...launchSettingRawValue,
      id: { value: launchSettingRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LaunchSettingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      launchAt: currentTime,
      active: false,
    };
  }

  private convertLaunchSettingRawValueToLaunchSetting(
    rawLaunchSetting: LaunchSettingFormRawValue | NewLaunchSettingFormRawValue,
  ): ILaunchSetting | NewLaunchSetting {
    return {
      ...rawLaunchSetting,
      launchAt: dayjs(rawLaunchSetting.launchAt, DATE_TIME_FORMAT),
    };
  }

  private convertLaunchSettingToLaunchSettingRawValue(
    launchSetting: ILaunchSetting | (Partial<NewLaunchSetting> & LaunchSettingFormDefaults),
  ): LaunchSettingFormRawValue | PartialWithRequiredKeyOf<NewLaunchSettingFormRawValue> {
    return {
      ...launchSetting,
      launchAt: launchSetting.launchAt ? launchSetting.launchAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
