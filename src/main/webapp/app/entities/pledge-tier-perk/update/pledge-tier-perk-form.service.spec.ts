import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../pledge-tier-perk.test-samples';

import { PledgeTierPerkFormService } from './pledge-tier-perk-form.service';

describe('PledgeTierPerk Form Service', () => {
  let service: PledgeTierPerkFormService;

  beforeEach(() => {
    service = TestBed.inject(PledgeTierPerkFormService);
  });

  describe('Service methods', () => {
    describe('createPledgeTierPerkFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPledgeTierPerkFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            displayOrder: expect.any(Object),
            tier: expect.any(Object),
          }),
        );
      });

      it('passing IPledgeTierPerk should create a new form with FormGroup', () => {
        const formGroup = service.createPledgeTierPerkFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            displayOrder: expect.any(Object),
            tier: expect.any(Object),
          }),
        );
      });
    });

    describe('getPledgeTierPerk', () => {
      it('should return NewPledgeTierPerk for default PledgeTierPerk initial value', () => {
        const formGroup = service.createPledgeTierPerkFormGroup(sampleWithNewData);

        const pledgeTierPerk = service.getPledgeTierPerk(formGroup);

        expect(pledgeTierPerk).toMatchObject(sampleWithNewData);
      });

      it('should return NewPledgeTierPerk for empty PledgeTierPerk initial value', () => {
        const formGroup = service.createPledgeTierPerkFormGroup();

        const pledgeTierPerk = service.getPledgeTierPerk(formGroup);

        expect(pledgeTierPerk).toMatchObject({});
      });

      it('should return IPledgeTierPerk', () => {
        const formGroup = service.createPledgeTierPerkFormGroup(sampleWithRequiredData);

        const pledgeTierPerk = service.getPledgeTierPerk(formGroup);

        expect(pledgeTierPerk).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPledgeTierPerk should not enable id FormControl', () => {
        const formGroup = service.createPledgeTierPerkFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPledgeTierPerk should disable id FormControl', () => {
        const formGroup = service.createPledgeTierPerkFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
