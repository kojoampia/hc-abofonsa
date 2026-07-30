import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../pledge-tier-teaser.test-samples';

import { PledgeTierTeaserFormService } from './pledge-tier-teaser-form.service';

describe('PledgeTierTeaser Form Service', () => {
  let service: PledgeTierTeaserFormService;

  beforeEach(() => {
    service = TestBed.inject(PledgeTierTeaserFormService);
  });

  describe('Service methods', () => {
    describe('createPledgeTierTeaserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            blurb: expect.any(Object),
            amount: expect.any(Object),
            currency: expect.any(Object),
            voucherValue: expect.any(Object),
            handoffUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });

      it('passing IPledgeTierTeaser should create a new form with FormGroup', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            blurb: expect.any(Object),
            amount: expect.any(Object),
            currency: expect.any(Object),
            voucherValue: expect.any(Object),
            handoffUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });
    });

    describe('getPledgeTierTeaser', () => {
      it('should return NewPledgeTierTeaser for default PledgeTierTeaser initial value', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup(sampleWithNewData);

        const pledgeTierTeaser = service.getPledgeTierTeaser(formGroup);

        expect(pledgeTierTeaser).toMatchObject(sampleWithNewData);
      });

      it('should return NewPledgeTierTeaser for empty PledgeTierTeaser initial value', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup();

        const pledgeTierTeaser = service.getPledgeTierTeaser(formGroup);

        expect(pledgeTierTeaser).toMatchObject({});
      });

      it('should return IPledgeTierTeaser', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup(sampleWithRequiredData);

        const pledgeTierTeaser = service.getPledgeTierTeaser(formGroup);

        expect(pledgeTierTeaser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPledgeTierTeaser should not enable id FormControl', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPledgeTierTeaser should disable id FormControl', () => {
        const formGroup = service.createPledgeTierTeaserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
