import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IServiceHighlight } from '../service-highlight.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../service-highlight.test-samples';

import { ServiceHighlightService } from './service-highlight.service';

const requireRestSample: IServiceHighlight = {
  ...sampleWithRequiredData,
};

describe('ServiceHighlight Service', () => {
  let service: ServiceHighlightService;
  let httpMock: HttpTestingController;
  let expectedResult: IServiceHighlight | IServiceHighlight[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ServiceHighlightService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ServiceHighlight', () => {
      const serviceHighlight = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(serviceHighlight).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ServiceHighlight', () => {
      const serviceHighlight = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(serviceHighlight).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ServiceHighlight', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ServiceHighlight', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ServiceHighlight', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addServiceHighlightToCollectionIfMissing', () => {
      it('should add a ServiceHighlight to an empty array', () => {
        const serviceHighlight: IServiceHighlight = sampleWithRequiredData;
        expectedResult = service.addServiceHighlightToCollectionIfMissing([], serviceHighlight);
        expect(expectedResult).toEqual([serviceHighlight]);
      });

      it('should not add a ServiceHighlight to an array that contains it', () => {
        const serviceHighlight: IServiceHighlight = sampleWithRequiredData;
        const serviceHighlightCollection: IServiceHighlight[] = [
          {
            ...serviceHighlight,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addServiceHighlightToCollectionIfMissing(serviceHighlightCollection, serviceHighlight);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ServiceHighlight to an array that doesn't contain it", () => {
        const serviceHighlight: IServiceHighlight = sampleWithRequiredData;
        const serviceHighlightCollection: IServiceHighlight[] = [sampleWithPartialData];
        expectedResult = service.addServiceHighlightToCollectionIfMissing(serviceHighlightCollection, serviceHighlight);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serviceHighlight);
      });

      it('should add only unique ServiceHighlight to an array', () => {
        const serviceHighlightArray: IServiceHighlight[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const serviceHighlightCollection: IServiceHighlight[] = [sampleWithRequiredData];
        expectedResult = service.addServiceHighlightToCollectionIfMissing(serviceHighlightCollection, ...serviceHighlightArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const serviceHighlight: IServiceHighlight = sampleWithRequiredData;
        const serviceHighlight2: IServiceHighlight = sampleWithPartialData;
        expectedResult = service.addServiceHighlightToCollectionIfMissing([], serviceHighlight, serviceHighlight2);
        expect(expectedResult).toEqual([serviceHighlight, serviceHighlight2]);
      });

      it('should accept null and undefined values', () => {
        const serviceHighlight: IServiceHighlight = sampleWithRequiredData;
        expectedResult = service.addServiceHighlightToCollectionIfMissing([], null, serviceHighlight, undefined);
        expect(expectedResult).toEqual([serviceHighlight]);
      });

      it('should return initial array if no ServiceHighlight is added', () => {
        const serviceHighlightCollection: IServiceHighlight[] = [sampleWithRequiredData];
        expectedResult = service.addServiceHighlightToCollectionIfMissing(serviceHighlightCollection, undefined, null);
        expect(expectedResult).toEqual(serviceHighlightCollection);
      });
    });

    describe('compareServiceHighlight', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareServiceHighlight(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17767 };
        const entity2 = null;

        const compareResult1 = service.compareServiceHighlight(entity1, entity2);
        const compareResult2 = service.compareServiceHighlight(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17767 };
        const entity2 = { id: 924 };

        const compareResult1 = service.compareServiceHighlight(entity1, entity2);
        const compareResult2 = service.compareServiceHighlight(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17767 };
        const entity2 = { id: 17767 };

        const compareResult1 = service.compareServiceHighlight(entity1, entity2);
        const compareResult2 = service.compareServiceHighlight(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
