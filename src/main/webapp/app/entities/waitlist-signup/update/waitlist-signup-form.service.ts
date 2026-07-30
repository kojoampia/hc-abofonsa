import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWaitlistSignup, NewWaitlistSignup } from '../waitlist-signup.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWaitlistSignup for edit and NewWaitlistSignupFormGroupInput for create.
 */
type WaitlistSignupFormGroupInput = IWaitlistSignup | PartialWithRequiredKeyOf<NewWaitlistSignup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWaitlistSignup | NewWaitlistSignup> = Omit<T, 'confirmedAt' | 'unsubscribedAt' | 'capturedAt'> & {
  confirmedAt?: string | null;
  unsubscribedAt?: string | null;
  capturedAt?: string | null;
};

type WaitlistSignupFormRawValue = FormValueOf<IWaitlistSignup>;

type NewWaitlistSignupFormRawValue = FormValueOf<NewWaitlistSignup>;

type WaitlistSignupFormDefaults = Pick<NewWaitlistSignup, 'id' | 'consentGiven' | 'confirmedAt' | 'unsubscribedAt' | 'capturedAt'>;

type WaitlistSignupFormGroupContent = {
  id: FormControl<WaitlistSignupFormRawValue['id'] | NewWaitlistSignup['id']>;
  email: FormControl<WaitlistSignupFormRawValue['email']>;
  emailNormalized: FormControl<WaitlistSignupFormRawValue['emailNormalized']>;
  fullName: FormControl<WaitlistSignupFormRawValue['fullName']>;
  organisation: FormControl<WaitlistSignupFormRawValue['organisation']>;
  audience: FormControl<WaitlistSignupFormRawValue['audience']>;
  planOfInterest: FormControl<WaitlistSignupFormRawValue['planOfInterest']>;
  status: FormControl<WaitlistSignupFormRawValue['status']>;
  locale: FormControl<WaitlistSignupFormRawValue['locale']>;
  sourcePage: FormControl<WaitlistSignupFormRawValue['sourcePage']>;
  utmSource: FormControl<WaitlistSignupFormRawValue['utmSource']>;
  utmMedium: FormControl<WaitlistSignupFormRawValue['utmMedium']>;
  utmCampaign: FormControl<WaitlistSignupFormRawValue['utmCampaign']>;
  referrer: FormControl<WaitlistSignupFormRawValue['referrer']>;
  deviceType: FormControl<WaitlistSignupFormRawValue['deviceType']>;
  consentGiven: FormControl<WaitlistSignupFormRawValue['consentGiven']>;
  confirmationToken: FormControl<WaitlistSignupFormRawValue['confirmationToken']>;
  confirmedAt: FormControl<WaitlistSignupFormRawValue['confirmedAt']>;
  unsubscribedAt: FormControl<WaitlistSignupFormRawValue['unsubscribedAt']>;
  capturedAt: FormControl<WaitlistSignupFormRawValue['capturedAt']>;
  ipHash: FormControl<WaitlistSignupFormRawValue['ipHash']>;
  userAgent: FormControl<WaitlistSignupFormRawValue['userAgent']>;
};

export type WaitlistSignupFormGroup = FormGroup<WaitlistSignupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WaitlistSignupFormService {
  createWaitlistSignupFormGroup(waitlistSignup?: WaitlistSignupFormGroupInput): WaitlistSignupFormGroup {
    const waitlistSignupRawValue = this.convertWaitlistSignupToWaitlistSignupRawValue({
      ...this.getFormDefaults(),
      ...(waitlistSignup ?? { id: null }),
    });
    return new FormGroup<WaitlistSignupFormGroupContent>({
      id: new FormControl(
        { value: waitlistSignupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      email: new FormControl(waitlistSignupRawValue.email, {
        validators: [
          Validators.required,
          Validators.maxLength(254),
          Validators.pattern('^[^@]+@[^@]+[.][^@]+$'), // NOSONAR
        ],
      }),
      emailNormalized: new FormControl(waitlistSignupRawValue.emailNormalized, {
        validators: [Validators.required, Validators.maxLength(254)],
      }),
      fullName: new FormControl(waitlistSignupRawValue.fullName, {
        validators: [Validators.maxLength(120)],
      }),
      organisation: new FormControl(waitlistSignupRawValue.organisation, {
        validators: [Validators.maxLength(160)],
      }),
      audience: new FormControl(waitlistSignupRawValue.audience),
      planOfInterest: new FormControl(waitlistSignupRawValue.planOfInterest),
      status: new FormControl(waitlistSignupRawValue.status, {
        validators: [Validators.required],
      }),
      locale: new FormControl(waitlistSignupRawValue.locale, {
        validators: [Validators.maxLength(10)],
      }),
      sourcePage: new FormControl(waitlistSignupRawValue.sourcePage, {
        validators: [Validators.maxLength(255)],
      }),
      utmSource: new FormControl(waitlistSignupRawValue.utmSource, {
        validators: [Validators.maxLength(120)],
      }),
      utmMedium: new FormControl(waitlistSignupRawValue.utmMedium, {
        validators: [Validators.maxLength(120)],
      }),
      utmCampaign: new FormControl(waitlistSignupRawValue.utmCampaign, {
        validators: [Validators.maxLength(120)],
      }),
      referrer: new FormControl(waitlistSignupRawValue.referrer, {
        validators: [Validators.maxLength(512)],
      }),
      deviceType: new FormControl(waitlistSignupRawValue.deviceType),
      consentGiven: new FormControl(waitlistSignupRawValue.consentGiven, {
        validators: [Validators.required],
      }),
      confirmationToken: new FormControl(waitlistSignupRawValue.confirmationToken, {
        validators: [Validators.maxLength(64)],
      }),
      confirmedAt: new FormControl(waitlistSignupRawValue.confirmedAt),
      unsubscribedAt: new FormControl(waitlistSignupRawValue.unsubscribedAt),
      capturedAt: new FormControl(waitlistSignupRawValue.capturedAt, {
        validators: [Validators.required],
      }),
      ipHash: new FormControl(waitlistSignupRawValue.ipHash, {
        validators: [Validators.maxLength(64)],
      }),
      userAgent: new FormControl(waitlistSignupRawValue.userAgent, {
        validators: [Validators.maxLength(512)],
      }),
    });
  }

  getWaitlistSignup(form: WaitlistSignupFormGroup): IWaitlistSignup | NewWaitlistSignup {
    return this.convertWaitlistSignupRawValueToWaitlistSignup(form.getRawValue());
  }

  resetForm(form: WaitlistSignupFormGroup, waitlistSignup: WaitlistSignupFormGroupInput): void {
    const waitlistSignupRawValue = this.convertWaitlistSignupToWaitlistSignupRawValue({ ...this.getFormDefaults(), ...waitlistSignup });
    form.reset({
      ...waitlistSignupRawValue,
      id: { value: waitlistSignupRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): WaitlistSignupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      consentGiven: false,
      confirmedAt: currentTime,
      unsubscribedAt: currentTime,
      capturedAt: currentTime,
    };
  }

  private convertWaitlistSignupRawValueToWaitlistSignup(
    rawWaitlistSignup: WaitlistSignupFormRawValue | NewWaitlistSignupFormRawValue,
  ): IWaitlistSignup | NewWaitlistSignup {
    return {
      ...rawWaitlistSignup,
      confirmedAt: dayjs(rawWaitlistSignup.confirmedAt, DATE_TIME_FORMAT),
      unsubscribedAt: dayjs(rawWaitlistSignup.unsubscribedAt, DATE_TIME_FORMAT),
      capturedAt: dayjs(rawWaitlistSignup.capturedAt, DATE_TIME_FORMAT),
    };
  }

  private convertWaitlistSignupToWaitlistSignupRawValue(
    waitlistSignup: IWaitlistSignup | (Partial<NewWaitlistSignup> & WaitlistSignupFormDefaults),
  ): WaitlistSignupFormRawValue | PartialWithRequiredKeyOf<NewWaitlistSignupFormRawValue> {
    return {
      ...waitlistSignup,
      confirmedAt: waitlistSignup.confirmedAt ? waitlistSignup.confirmedAt.format(DATE_TIME_FORMAT) : undefined,
      unsubscribedAt: waitlistSignup.unsubscribedAt ? waitlistSignup.unsubscribedAt.format(DATE_TIME_FORMAT) : undefined,
      capturedAt: waitlistSignup.capturedAt ? waitlistSignup.capturedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
