import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMetricRollup } from '../metric-rollup.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../metric-rollup.test-samples';

import { MetricRollupService, RestMetricRollup } from './metric-rollup.service';

const requireRestSample: RestMetricRollup = {
  ...sampleWithRequiredData,
  bucketStart: sampleWithRequiredData.bucketStart?.toJSON(),
  bucketEnd: sampleWithRequiredData.bucketEnd?.toJSON(),
  computedAt: sampleWithRequiredData.computedAt?.toJSON(),
};

describe('MetricRollup Service', () => {
  let service: MetricRollupService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetricRollup | IMetricRollup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MetricRollupService);
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

    it('should return a list of MetricRollup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addMetricRollupToCollectionIfMissing', () => {
      it('should add a MetricRollup to an empty array', () => {
        const metricRollup: IMetricRollup = sampleWithRequiredData;
        expectedResult = service.addMetricRollupToCollectionIfMissing([], metricRollup);
        expect(expectedResult).toEqual([metricRollup]);
      });

      it('should not add a MetricRollup to an array that contains it', () => {
        const metricRollup: IMetricRollup = sampleWithRequiredData;
        const metricRollupCollection: IMetricRollup[] = [
          {
            ...metricRollup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetricRollupToCollectionIfMissing(metricRollupCollection, metricRollup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MetricRollup to an array that doesn't contain it", () => {
        const metricRollup: IMetricRollup = sampleWithRequiredData;
        const metricRollupCollection: IMetricRollup[] = [sampleWithPartialData];
        expectedResult = service.addMetricRollupToCollectionIfMissing(metricRollupCollection, metricRollup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metricRollup);
      });

      it('should add only unique MetricRollup to an array', () => {
        const metricRollupArray: IMetricRollup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metricRollupCollection: IMetricRollup[] = [sampleWithRequiredData];
        expectedResult = service.addMetricRollupToCollectionIfMissing(metricRollupCollection, ...metricRollupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metricRollup: IMetricRollup = sampleWithRequiredData;
        const metricRollup2: IMetricRollup = sampleWithPartialData;
        expectedResult = service.addMetricRollupToCollectionIfMissing([], metricRollup, metricRollup2);
        expect(expectedResult).toEqual([metricRollup, metricRollup2]);
      });

      it('should accept null and undefined values', () => {
        const metricRollup: IMetricRollup = sampleWithRequiredData;
        expectedResult = service.addMetricRollupToCollectionIfMissing([], null, metricRollup, undefined);
        expect(expectedResult).toEqual([metricRollup]);
      });

      it('should return initial array if no MetricRollup is added', () => {
        const metricRollupCollection: IMetricRollup[] = [sampleWithRequiredData];
        expectedResult = service.addMetricRollupToCollectionIfMissing(metricRollupCollection, undefined, null);
        expect(expectedResult).toEqual(metricRollupCollection);
      });
    });

    describe('compareMetricRollup', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetricRollup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 5090 };
        const entity2 = null;

        const compareResult1 = service.compareMetricRollup(entity1, entity2);
        const compareResult2 = service.compareMetricRollup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 5090 };
        const entity2 = { id: 4603 };

        const compareResult1 = service.compareMetricRollup(entity1, entity2);
        const compareResult2 = service.compareMetricRollup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 5090 };
        const entity2 = { id: 5090 };

        const compareResult1 = service.compareMetricRollup(entity1, entity2);
        const compareResult2 = service.compareMetricRollup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
