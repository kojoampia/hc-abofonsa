import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICarePlanTeaser } from '../care-plan-teaser.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../care-plan-teaser.test-samples';

import { CarePlanTeaserService } from './care-plan-teaser.service';

const requireRestSample: ICarePlanTeaser = {
  ...sampleWithRequiredData,
};

describe('CarePlanTeaser Service', () => {
  let service: CarePlanTeaserService;
  let httpMock: HttpTestingController;
  let expectedResult: ICarePlanTeaser | ICarePlanTeaser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CarePlanTeaserService);
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

    it('should create a CarePlanTeaser', () => {
      const carePlanTeaser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(carePlanTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CarePlanTeaser', () => {
      const carePlanTeaser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(carePlanTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CarePlanTeaser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CarePlanTeaser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CarePlanTeaser', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCarePlanTeaserToCollectionIfMissing', () => {
      it('should add a CarePlanTeaser to an empty array', () => {
        const carePlanTeaser: ICarePlanTeaser = sampleWithRequiredData;
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing([], carePlanTeaser);
        expect(expectedResult).toEqual([carePlanTeaser]);
      });

      it('should not add a CarePlanTeaser to an array that contains it', () => {
        const carePlanTeaser: ICarePlanTeaser = sampleWithRequiredData;
        const carePlanTeaserCollection: ICarePlanTeaser[] = [
          {
            ...carePlanTeaser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing(carePlanTeaserCollection, carePlanTeaser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CarePlanTeaser to an array that doesn't contain it", () => {
        const carePlanTeaser: ICarePlanTeaser = sampleWithRequiredData;
        const carePlanTeaserCollection: ICarePlanTeaser[] = [sampleWithPartialData];
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing(carePlanTeaserCollection, carePlanTeaser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(carePlanTeaser);
      });

      it('should add only unique CarePlanTeaser to an array', () => {
        const carePlanTeaserArray: ICarePlanTeaser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const carePlanTeaserCollection: ICarePlanTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing(carePlanTeaserCollection, ...carePlanTeaserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const carePlanTeaser: ICarePlanTeaser = sampleWithRequiredData;
        const carePlanTeaser2: ICarePlanTeaser = sampleWithPartialData;
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing([], carePlanTeaser, carePlanTeaser2);
        expect(expectedResult).toEqual([carePlanTeaser, carePlanTeaser2]);
      });

      it('should accept null and undefined values', () => {
        const carePlanTeaser: ICarePlanTeaser = sampleWithRequiredData;
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing([], null, carePlanTeaser, undefined);
        expect(expectedResult).toEqual([carePlanTeaser]);
      });

      it('should return initial array if no CarePlanTeaser is added', () => {
        const carePlanTeaserCollection: ICarePlanTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addCarePlanTeaserToCollectionIfMissing(carePlanTeaserCollection, undefined, null);
        expect(expectedResult).toEqual(carePlanTeaserCollection);
      });
    });

    describe('compareCarePlanTeaser', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCarePlanTeaser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16096 };
        const entity2 = null;

        const compareResult1 = service.compareCarePlanTeaser(entity1, entity2);
        const compareResult2 = service.compareCarePlanTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16096 };
        const entity2 = { id: 3687 };

        const compareResult1 = service.compareCarePlanTeaser(entity1, entity2);
        const compareResult2 = service.compareCarePlanTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16096 };
        const entity2 = { id: 16096 };

        const compareResult1 = service.compareCarePlanTeaser(entity1, entity2);
        const compareResult2 = service.compareCarePlanTeaser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
