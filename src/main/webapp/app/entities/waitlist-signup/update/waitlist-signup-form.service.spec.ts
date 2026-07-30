import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../waitlist-signup.test-samples';

import { WaitlistSignupFormService } from './waitlist-signup-form.service';

describe('WaitlistSignup Form Service', () => {
  let service: WaitlistSignupFormService;

  beforeEach(() => {
    service = TestBed.inject(WaitlistSignupFormService);
  });

  describe('Service methods', () => {
    describe('createWaitlistSignupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWaitlistSignupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            email: expect.any(Object),
            emailNormalized: expect.any(Object),
            fullName: expect.any(Object),
            organisation: expect.any(Object),
            audience: expect.any(Object),
            planOfInterest: expect.any(Object),
            status: expect.any(Object),
            locale: expect.any(Object),
            sourcePage: expect.any(Object),
            utmSource: expect.any(Object),
            utmMedium: expect.any(Object),
            utmCampaign: expect.any(Object),
            referrer: expect.any(Object),
            deviceType: expect.any(Object),
            consentGiven: expect.any(Object),
            confirmationToken: expect.any(Object),
            confirmedAt: expect.any(Object),
            unsubscribedAt: expect.any(Object),
            capturedAt: expect.any(Object),
            ipHash: expect.any(Object),
            userAgent: expect.any(Object),
          }),
        );
      });

      it('passing IWaitlistSignup should create a new form with FormGroup', () => {
        const formGroup = service.createWaitlistSignupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            email: expect.any(Object),
            emailNormalized: expect.any(Object),
            fullName: expect.any(Object),
            organisation: expect.any(Object),
            audience: expect.any(Object),
            planOfInterest: expect.any(Object),
            status: expect.any(Object),
            locale: expect.any(Object),
            sourcePage: expect.any(Object),
            utmSource: expect.any(Object),
            utmMedium: expect.any(Object),
            utmCampaign: expect.any(Object),
            referrer: expect.any(Object),
            deviceType: expect.any(Object),
            consentGiven: expect.any(Object),
            confirmationToken: expect.any(Object),
            confirmedAt: expect.any(Object),
            unsubscribedAt: expect.any(Object),
            capturedAt: expect.any(Object),
            ipHash: expect.any(Object),
            userAgent: expect.any(Object),
          }),
        );
      });
    });

    describe('getWaitlistSignup', () => {
      it('should return NewWaitlistSignup for default WaitlistSignup initial value', () => {
        const formGroup = service.createWaitlistSignupFormGroup(sampleWithNewData);

        const waitlistSignup = service.getWaitlistSignup(formGroup);

        expect(waitlistSignup).toMatchObject(sampleWithNewData);
      });

      it('should return NewWaitlistSignup for empty WaitlistSignup initial value', () => {
        const formGroup = service.createWaitlistSignupFormGroup();

        const waitlistSignup = service.getWaitlistSignup(formGroup);

        expect(waitlistSignup).toMatchObject({});
      });

      it('should return IWaitlistSignup', () => {
        const formGroup = service.createWaitlistSignupFormGroup(sampleWithRequiredData);

        const waitlistSignup = service.getWaitlistSignup(formGroup);

        expect(waitlistSignup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWaitlistSignup should not enable id FormControl', () => {
        const formGroup = service.createWaitlistSignupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWaitlistSignup should disable id FormControl', () => {
        const formGroup = service.createWaitlistSignupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
