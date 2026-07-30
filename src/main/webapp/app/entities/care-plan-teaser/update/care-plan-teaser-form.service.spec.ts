import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../care-plan-teaser.test-samples';

import { CarePlanTeaserFormService } from './care-plan-teaser-form.service';

describe('CarePlanTeaser Form Service', () => {
  let service: CarePlanTeaserFormService;

  beforeEach(() => {
    service = TestBed.inject(CarePlanTeaserFormService);
  });

  describe('Service methods', () => {
    describe('createCarePlanTeaserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCarePlanTeaserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            forWho: expect.any(Object),
            priceAmount: expect.any(Object),
            priceCurrency: expect.any(Object),
            pricePeriod: expect.any(Object),
            priceNote: expect.any(Object),
            featured: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });

      it('passing ICarePlanTeaser should create a new form with FormGroup', () => {
        const formGroup = service.createCarePlanTeaserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            forWho: expect.any(Object),
            priceAmount: expect.any(Object),
            priceCurrency: expect.any(Object),
            pricePeriod: expect.any(Object),
            priceNote: expect.any(Object),
            featured: expect.any(Object),
            displayOrder: expect.any(Object),
            published: expect.any(Object),
          }),
        );
      });
    });

    describe('getCarePlanTeaser', () => {
      it('should return NewCarePlanTeaser for default CarePlanTeaser initial value', () => {
        const formGroup = service.createCarePlanTeaserFormGroup(sampleWithNewData);

        const carePlanTeaser = service.getCarePlanTeaser(formGroup);

        expect(carePlanTeaser).toMatchObject(sampleWithNewData);
      });

      it('should return NewCarePlanTeaser for empty CarePlanTeaser initial value', () => {
        const formGroup = service.createCarePlanTeaserFormGroup();

        const carePlanTeaser = service.getCarePlanTeaser(formGroup);

        expect(carePlanTeaser).toMatchObject({});
      });

      it('should return ICarePlanTeaser', () => {
        const formGroup = service.createCarePlanTeaserFormGroup(sampleWithRequiredData);

        const carePlanTeaser = service.getCarePlanTeaser(formGroup);

        expect(carePlanTeaser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICarePlanTeaser should not enable id FormControl', () => {
        const formGroup = service.createCarePlanTeaserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCarePlanTeaser should disable id FormControl', () => {
        const formGroup = service.createCarePlanTeaserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
