import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../launch-milestone.test-samples';

import { LaunchMilestoneFormService } from './launch-milestone-form.service';

describe('LaunchMilestone Form Service', () => {
  let service: LaunchMilestoneFormService;

  beforeEach(() => {
    service = TestBed.inject(LaunchMilestoneFormService);
  });

  describe('Service methods', () => {
    describe('createLaunchMilestoneFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLaunchMilestoneFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phaseLabel: expect.any(Object),
            title: expect.any(Object),
            body: expect.any(Object),
            milestoneDate: expect.any(Object),
            current: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });

      it('passing ILaunchMilestone should create a new form with FormGroup', () => {
        const formGroup = service.createLaunchMilestoneFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phaseLabel: expect.any(Object),
            title: expect.any(Object),
            body: expect.any(Object),
            milestoneDate: expect.any(Object),
            current: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });
    });

    describe('getLaunchMilestone', () => {
      it('should return NewLaunchMilestone for default LaunchMilestone initial value', () => {
        const formGroup = service.createLaunchMilestoneFormGroup(sampleWithNewData);

        const launchMilestone = service.getLaunchMilestone(formGroup);

        expect(launchMilestone).toMatchObject(sampleWithNewData);
      });

      it('should return NewLaunchMilestone for empty LaunchMilestone initial value', () => {
        const formGroup = service.createLaunchMilestoneFormGroup();

        const launchMilestone = service.getLaunchMilestone(formGroup);

        expect(launchMilestone).toMatchObject({});
      });

      it('should return ILaunchMilestone', () => {
        const formGroup = service.createLaunchMilestoneFormGroup(sampleWithRequiredData);

        const launchMilestone = service.getLaunchMilestone(formGroup);

        expect(launchMilestone).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILaunchMilestone should not enable id FormControl', () => {
        const formGroup = service.createLaunchMilestoneFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLaunchMilestone should disable id FormControl', () => {
        const formGroup = service.createLaunchMilestoneFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
