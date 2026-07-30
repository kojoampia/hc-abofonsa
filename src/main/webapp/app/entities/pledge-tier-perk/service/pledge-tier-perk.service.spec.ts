import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPledgeTierPerk } from '../pledge-tier-perk.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../pledge-tier-perk.test-samples';

import { PledgeTierPerkService } from './pledge-tier-perk.service';

const requireRestSample: IPledgeTierPerk = {
  ...sampleWithRequiredData,
};

describe('PledgeTierPerk Service', () => {
  let service: PledgeTierPerkService;
  let httpMock: HttpTestingController;
  let expectedResult: IPledgeTierPerk | IPledgeTierPerk[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PledgeTierPerkService);
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

    it('should create a PledgeTierPerk', () => {
      const pledgeTierPerk = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pledgeTierPerk).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PledgeTierPerk', () => {
      const pledgeTierPerk = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pledgeTierPerk).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PledgeTierPerk', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PledgeTierPerk', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PledgeTierPerk', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPledgeTierPerkToCollectionIfMissing', () => {
      it('should add a PledgeTierPerk to an empty array', () => {
        const pledgeTierPerk: IPledgeTierPerk = sampleWithRequiredData;
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing([], pledgeTierPerk);
        expect(expectedResult).toEqual([pledgeTierPerk]);
      });

      it('should not add a PledgeTierPerk to an array that contains it', () => {
        const pledgeTierPerk: IPledgeTierPerk = sampleWithRequiredData;
        const pledgeTierPerkCollection: IPledgeTierPerk[] = [
          {
            ...pledgeTierPerk,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing(pledgeTierPerkCollection, pledgeTierPerk);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PledgeTierPerk to an array that doesn't contain it", () => {
        const pledgeTierPerk: IPledgeTierPerk = sampleWithRequiredData;
        const pledgeTierPerkCollection: IPledgeTierPerk[] = [sampleWithPartialData];
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing(pledgeTierPerkCollection, pledgeTierPerk);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pledgeTierPerk);
      });

      it('should add only unique PledgeTierPerk to an array', () => {
        const pledgeTierPerkArray: IPledgeTierPerk[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pledgeTierPerkCollection: IPledgeTierPerk[] = [sampleWithRequiredData];
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing(pledgeTierPerkCollection, ...pledgeTierPerkArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pledgeTierPerk: IPledgeTierPerk = sampleWithRequiredData;
        const pledgeTierPerk2: IPledgeTierPerk = sampleWithPartialData;
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing([], pledgeTierPerk, pledgeTierPerk2);
        expect(expectedResult).toEqual([pledgeTierPerk, pledgeTierPerk2]);
      });

      it('should accept null and undefined values', () => {
        const pledgeTierPerk: IPledgeTierPerk = sampleWithRequiredData;
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing([], null, pledgeTierPerk, undefined);
        expect(expectedResult).toEqual([pledgeTierPerk]);
      });

      it('should return initial array if no PledgeTierPerk is added', () => {
        const pledgeTierPerkCollection: IPledgeTierPerk[] = [sampleWithRequiredData];
        expectedResult = service.addPledgeTierPerkToCollectionIfMissing(pledgeTierPerkCollection, undefined, null);
        expect(expectedResult).toEqual(pledgeTierPerkCollection);
      });
    });

    describe('comparePledgeTierPerk', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePledgeTierPerk(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23265 };
        const entity2 = null;

        const compareResult1 = service.comparePledgeTierPerk(entity1, entity2);
        const compareResult2 = service.comparePledgeTierPerk(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23265 };
        const entity2 = { id: 31987 };

        const compareResult1 = service.comparePledgeTierPerk(entity1, entity2);
        const compareResult2 = service.comparePledgeTierPerk(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23265 };
        const entity2 = { id: 23265 };

        const compareResult1 = service.comparePledgeTierPerk(entity1, entity2);
        const compareResult2 = service.comparePledgeTierPerk(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
