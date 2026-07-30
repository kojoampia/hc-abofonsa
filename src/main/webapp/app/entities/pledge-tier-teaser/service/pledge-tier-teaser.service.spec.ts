import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPledgeTierTeaser } from '../pledge-tier-teaser.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../pledge-tier-teaser.test-samples';

import { PledgeTierTeaserService } from './pledge-tier-teaser.service';

const requireRestSample: IPledgeTierTeaser = {
  ...sampleWithRequiredData,
};

describe('PledgeTierTeaser Service', () => {
  let service: PledgeTierTeaserService;
  let httpMock: HttpTestingController;
  let expectedResult: IPledgeTierTeaser | IPledgeTierTeaser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PledgeTierTeaserService);
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

    it('should create a PledgeTierTeaser', () => {
      const pledgeTierTeaser = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pledgeTierTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PledgeTierTeaser', () => {
      const pledgeTierTeaser = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pledgeTierTeaser).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PledgeTierTeaser', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PledgeTierTeaser', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PledgeTierTeaser', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPledgeTierTeaserToCollectionIfMissing', () => {
      it('should add a PledgeTierTeaser to an empty array', () => {
        const pledgeTierTeaser: IPledgeTierTeaser = sampleWithRequiredData;
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing([], pledgeTierTeaser);
        expect(expectedResult).toEqual([pledgeTierTeaser]);
      });

      it('should not add a PledgeTierTeaser to an array that contains it', () => {
        const pledgeTierTeaser: IPledgeTierTeaser = sampleWithRequiredData;
        const pledgeTierTeaserCollection: IPledgeTierTeaser[] = [
          {
            ...pledgeTierTeaser,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing(pledgeTierTeaserCollection, pledgeTierTeaser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PledgeTierTeaser to an array that doesn't contain it", () => {
        const pledgeTierTeaser: IPledgeTierTeaser = sampleWithRequiredData;
        const pledgeTierTeaserCollection: IPledgeTierTeaser[] = [sampleWithPartialData];
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing(pledgeTierTeaserCollection, pledgeTierTeaser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pledgeTierTeaser);
      });

      it('should add only unique PledgeTierTeaser to an array', () => {
        const pledgeTierTeaserArray: IPledgeTierTeaser[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pledgeTierTeaserCollection: IPledgeTierTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing(pledgeTierTeaserCollection, ...pledgeTierTeaserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pledgeTierTeaser: IPledgeTierTeaser = sampleWithRequiredData;
        const pledgeTierTeaser2: IPledgeTierTeaser = sampleWithPartialData;
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing([], pledgeTierTeaser, pledgeTierTeaser2);
        expect(expectedResult).toEqual([pledgeTierTeaser, pledgeTierTeaser2]);
      });

      it('should accept null and undefined values', () => {
        const pledgeTierTeaser: IPledgeTierTeaser = sampleWithRequiredData;
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing([], null, pledgeTierTeaser, undefined);
        expect(expectedResult).toEqual([pledgeTierTeaser]);
      });

      it('should return initial array if no PledgeTierTeaser is added', () => {
        const pledgeTierTeaserCollection: IPledgeTierTeaser[] = [sampleWithRequiredData];
        expectedResult = service.addPledgeTierTeaserToCollectionIfMissing(pledgeTierTeaserCollection, undefined, null);
        expect(expectedResult).toEqual(pledgeTierTeaserCollection);
      });
    });

    describe('comparePledgeTierTeaser', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePledgeTierTeaser(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29268 };
        const entity2 = null;

        const compareResult1 = service.comparePledgeTierTeaser(entity1, entity2);
        const compareResult2 = service.comparePledgeTierTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29268 };
        const entity2 = { id: 4226 };

        const compareResult1 = service.comparePledgeTierTeaser(entity1, entity2);
        const compareResult2 = service.comparePledgeTierTeaser(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29268 };
        const entity2 = { id: 29268 };

        const compareResult1 = service.comparePledgeTierTeaser(entity1, entity2);
        const compareResult2 = service.comparePledgeTierTeaser(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
