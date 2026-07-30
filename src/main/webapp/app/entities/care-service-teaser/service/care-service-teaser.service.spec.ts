import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICareServiceTeaser } from '../care-service-teaser.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../care-service-teaser.test-samples';

import { CareServiceTeaserService } from './care-service-teaser.service';

const requireRestSample: ICareServiceTeaser = {
  ...sampleWithRequiredData,
};

describe('CareServiceTeaser Service', () => {
  let service: CareServiceTeaserService;
  let httpMock: HttpTestingController;
  let expectedResult: ICareServiceTeaser | ICareServiceTeaser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CareServiceTeaserService);
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

    it('should create a CareServiceTeaser', () => {
      const careServiceTeaser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(careServiceTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CareServiceTeaser', () => {
      const careServiceTeaser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(careServiceTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CareServiceTeaser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CareServiceTeaser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CareServiceTeaser', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCareServiceTeaserToCollectionIfMissing', () => {
      it('should add a CareServiceTeaser to an empty array', () => {
        const careServiceTeaser: ICareServiceTeaser = sampleWithRequiredData;
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing([], careServiceTeaser);
        expect(expectedResult).toEqual([careServiceTeaser]);
      });

      it('should not add a CareServiceTeaser to an array that contains it', () => {
        const careServiceTeaser: ICareServiceTeaser = sampleWithRequiredData;
        const careServiceTeaserCollection: ICareServiceTeaser[] = [
          {
            ...careServiceTeaser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing(careServiceTeaserCollection, careServiceTeaser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CareServiceTeaser to an array that doesn't contain it", () => {
        const careServiceTeaser: ICareServiceTeaser = sampleWithRequiredData;
        const careServiceTeaserCollection: ICareServiceTeaser[] = [sampleWithPartialData];
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing(careServiceTeaserCollection, careServiceTeaser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(careServiceTeaser);
      });

      it('should add only unique CareServiceTeaser to an array', () => {
        const careServiceTeaserArray: ICareServiceTeaser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const careServiceTeaserCollection: ICareServiceTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing(careServiceTeaserCollection, ...careServiceTeaserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const careServiceTeaser: ICareServiceTeaser = sampleWithRequiredData;
        const careServiceTeaser2: ICareServiceTeaser = sampleWithPartialData;
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing([], careServiceTeaser, careServiceTeaser2);
        expect(expectedResult).toEqual([careServiceTeaser, careServiceTeaser2]);
      });

      it('should accept null and undefined values', () => {
        const careServiceTeaser: ICareServiceTeaser = sampleWithRequiredData;
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing([], null, careServiceTeaser, undefined);
        expect(expectedResult).toEqual([careServiceTeaser]);
      });

      it('should return initial array if no CareServiceTeaser is added', () => {
        const careServiceTeaserCollection: ICareServiceTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addCareServiceTeaserToCollectionIfMissing(careServiceTeaserCollection, undefined, null);
        expect(expectedResult).toEqual(careServiceTeaserCollection);
      });
    });

    describe('compareCareServiceTeaser', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCareServiceTeaser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28585 };
        const entity2 = null;

        const compareResult1 = service.compareCareServiceTeaser(entity1, entity2);
        const compareResult2 = service.compareCareServiceTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28585 };
        const entity2 = { id: 22759 };

        const compareResult1 = service.compareCareServiceTeaser(entity1, entity2);
        const compareResult2 = service.compareCareServiceTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28585 };
        const entity2 = { id: 28585 };

        const compareResult1 = service.compareCareServiceTeaser(entity1, entity2);
        const compareResult2 = service.compareCareServiceTeaser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
