import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../care-service-teaser.test-samples';

import { CareServiceTeaserFormService } from './care-service-teaser-form.service';

describe('CareServiceTeaser Form Service', () => {
  let service: CareServiceTeaserFormService;

  beforeEach(() => {
    service = TestBed.inject(CareServiceTeaserFormService);
  });

  describe('Service methods', () => {
    describe('createCareServiceTeaserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCareServiceTeaserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            slug: expect.any(Object),
            name: expect.any(Object),
            blurb: expect.any(Object),
            iconKey: expect.any(Object),
            availableOn: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });

      it('passing ICareServiceTeaser should create a new form with FormGroup', () => {
        const formGroup = service.createCareServiceTeaserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            slug: expect.any(Object),
            name: expect.any(Object),
            blurb: expect.any(Object),
            iconKey: expect.any(Object),
            availableOn: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });
    });

    describe('getCareServiceTeaser', () => {
      it('should return NewCareServiceTeaser for default CareServiceTeaser initial value', () => {
        const formGroup = service.createCareServiceTeaserFormGroup(sampleWithNewData);

        const careServiceTeaser = service.getCareServiceTeaser(formGroup);

        expect(careServiceTeaser).toMatchObject(sampleWithNewData);
      });

      it('should return NewCareServiceTeaser for empty CareServiceTeaser initial value', () => {
        const formGroup = service.createCareServiceTeaserFormGroup();

        const careServiceTeaser = service.getCareServiceTeaser(formGroup);

        expect(careServiceTeaser).toMatchObject({});
      });

      it('should return ICareServiceTeaser', () => {
        const formGroup = service.createCareServiceTeaserFormGroup(sampleWithRequiredData);

        const careServiceTeaser = service.getCareServiceTeaser(formGroup);

        expect(careServiceTeaser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICareServiceTeaser should not enable id FormControl', () => {
        const formGroup = service.createCareServiceTeaserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCareServiceTeaser should disable id FormControl', () => {
        const formGroup = service.createCareServiceTeaserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
