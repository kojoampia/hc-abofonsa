import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../social-link.test-samples';

import { SocialLinkFormService } from './social-link-form.service';

describe('SocialLink Form Service', () => {
  let service: SocialLinkFormService;

  beforeEach(() => {
    service = TestBed.inject(SocialLinkFormService);
  });

  describe('Service methods', () => {
    describe('createSocialLinkFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSocialLinkFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            platform: expect.any(Object),
            label: expect.any(Object),
            url: expect.any(Object),
            iconKey: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });

      it('passing ISocialLink should create a new form with FormGroup', () => {
        const formGroup = service.createSocialLinkFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            platform: expect.any(Object),
            label: expect.any(Object),
            url: expect.any(Object),
            iconKey: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });
    });

    describe('getSocialLink', () => {
      it('should return NewSocialLink for default SocialLink initial value', () => {
        const formGroup = service.createSocialLinkFormGroup(sampleWithNewData);

        const socialLink = service.getSocialLink(formGroup);

        expect(socialLink).toMatchObject(sampleWithNewData);
      });

      it('should return NewSocialLink for empty SocialLink initial value', () => {
        const formGroup = service.createSocialLinkFormGroup();

        const socialLink = service.getSocialLink(formGroup);

        expect(socialLink).toMatchObject({});
      });

      it('should return ISocialLink', () => {
        const formGroup = service.createSocialLinkFormGroup(sampleWithRequiredData);

        const socialLink = service.getSocialLink(formGroup);

        expect(socialLink).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISocialLink should not enable id FormControl', () => {
        const formGroup = service.createSocialLinkFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSocialLink should disable id FormControl', () => {
        const formGroup = service.createSocialLinkFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
