import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../service-highlight.test-samples';

import { ServiceHighlightFormService } from './service-highlight-form.service';

describe('ServiceHighlight Form Service', () => {
  let service: ServiceHighlightFormService;

  beforeEach(() => {
    service = TestBed.inject(ServiceHighlightFormService);
  });

  describe('Service methods', () => {
    describe('createServiceHighlightFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createServiceHighlightFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            displayOrder: expect.any(Object),
            service: expect.any(Object),
          }),
        );
      });

      it('passing IServiceHighlight should create a new form with FormGroup', () => {
        const formGroup = service.createServiceHighlightFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            displayOrder: expect.any(Object),
            service: expect.any(Object),
          }),
        );
      });
    });

    describe('getServiceHighlight', () => {
      it('should return NewServiceHighlight for default ServiceHighlight initial value', () => {
        const formGroup = service.createServiceHighlightFormGroup(sampleWithNewData);

        const serviceHighlight = service.getServiceHighlight(formGroup);

        expect(serviceHighlight).toMatchObject(sampleWithNewData);
      });

      it('should return NewServiceHighlight for empty ServiceHighlight initial value', () => {
        const formGroup = service.createServiceHighlightFormGroup();

        const serviceHighlight = service.getServiceHighlight(formGroup);

        expect(serviceHighlight).toMatchObject({});
      });

      it('should return IServiceHighlight', () => {
        const formGroup = service.createServiceHighlightFormGroup(sampleWithRequiredData);

        const serviceHighlight = service.getServiceHighlight(formGroup);

        expect(serviceHighlight).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IServiceHighlight should not enable id FormControl', () => {
        const formGroup = service.createServiceHighlightFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewServiceHighlight should disable id FormControl', () => {
        const formGroup = service.createServiceHighlightFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
