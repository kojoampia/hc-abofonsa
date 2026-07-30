import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../launch-setting.test-samples';

import { LaunchSettingFormService } from './launch-setting-form.service';

describe('LaunchSetting Form Service', () => {
  let service: LaunchSettingFormService;

  beforeEach(() => {
    service = TestBed.inject(LaunchSettingFormService);
  });

  describe('Service methods', () => {
    describe('createLaunchSettingFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLaunchSettingFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            settingKey: expect.any(Object),
            organisationName: expect.any(Object),
            tagline: expect.any(Object),
            launchAt: expect.any(Object),
            launchTimezone: expect.any(Object),
            fundUrl: expect.any(Object),
            contactEmail: expect.any(Object),
            contactPhone: expect.any(Object),
            officeAddress: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });

      it('passing ILaunchSetting should create a new form with FormGroup', () => {
        const formGroup = service.createLaunchSettingFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            settingKey: expect.any(Object),
            organisationName: expect.any(Object),
            tagline: expect.any(Object),
            launchAt: expect.any(Object),
            launchTimezone: expect.any(Object),
            fundUrl: expect.any(Object),
            contactEmail: expect.any(Object),
            contactPhone: expect.any(Object),
            officeAddress: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });
    });

    describe('getLaunchSetting', () => {
      it('should return NewLaunchSetting for default LaunchSetting initial value', () => {
        const formGroup = service.createLaunchSettingFormGroup(sampleWithNewData);

        const launchSetting = service.getLaunchSetting(formGroup);

        expect(launchSetting).toMatchObject(sampleWithNewData);
      });

      it('should return NewLaunchSetting for empty LaunchSetting initial value', () => {
        const formGroup = service.createLaunchSettingFormGroup();

        const launchSetting = service.getLaunchSetting(formGroup);

        expect(launchSetting).toMatchObject({});
      });

      it('should return ILaunchSetting', () => {
        const formGroup = service.createLaunchSettingFormGroup(sampleWithRequiredData);

        const launchSetting = service.getLaunchSetting(formGroup);

        expect(launchSetting).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILaunchSetting should not enable id FormControl', () => {
        const formGroup = service.createLaunchSettingFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLaunchSetting should disable id FormControl', () => {
        const formGroup = service.createLaunchSettingFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
