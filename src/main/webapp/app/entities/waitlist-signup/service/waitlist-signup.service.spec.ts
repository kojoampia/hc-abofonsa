import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IWaitlistSignup } from '../waitlist-signup.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../waitlist-signup.test-samples';

import { RestWaitlistSignup, WaitlistSignupService } from './waitlist-signup.service';

const requireRestSample: RestWaitlistSignup = {
  ...sampleWithRequiredData,
  confirmedAt: sampleWithRequiredData.confirmedAt?.toJSON(),
  unsubscribedAt: sampleWithRequiredData.unsubscribedAt?.toJSON(),
  capturedAt: sampleWithRequiredData.capturedAt?.toJSON(),
};

describe('WaitlistSignup Service', () => {
  let service: WaitlistSignupService;
  let httpMock: HttpTestingController;
  let expectedResult: IWaitlistSignup | IWaitlistSignup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(WaitlistSignupService);
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

    it('should create a WaitlistSignup', () => {
      const waitlistSignup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(waitlistSignup).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WaitlistSignup', () => {
      const waitlistSignup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(waitlistSignup).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WaitlistSignup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WaitlistSignup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WaitlistSignup', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addWaitlistSignupToCollectionIfMissing', () => {
      it('should add a WaitlistSignup to an empty array', () => {
        const waitlistSignup: IWaitlistSignup = sampleWithRequiredData;
        expectedResult = service.addWaitlistSignupToCollectionIfMissing([], waitlistSignup);
        expect(expectedResult).toEqual([waitlistSignup]);
      });

      it('should not add a WaitlistSignup to an array that contains it', () => {
        const waitlistSignup: IWaitlistSignup = sampleWithRequiredData;
        const waitlistSignupCollection: IWaitlistSignup[] = [
          {
            ...waitlistSignup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWaitlistSignupToCollectionIfMissing(waitlistSignupCollection, waitlistSignup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WaitlistSignup to an array that doesn't contain it", () => {
        const waitlistSignup: IWaitlistSignup = sampleWithRequiredData;
        const waitlistSignupCollection: IWaitlistSignup[] = [sampleWithPartialData];
        expectedResult = service.addWaitlistSignupToCollectionIfMissing(waitlistSignupCollection, waitlistSignup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(waitlistSignup);
      });

      it('should add only unique WaitlistSignup to an array', () => {
        const waitlistSignupArray: IWaitlistSignup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const waitlistSignupCollection: IWaitlistSignup[] = [sampleWithRequiredData];
        expectedResult = service.addWaitlistSignupToCollectionIfMissing(waitlistSignupCollection, ...waitlistSignupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const waitlistSignup: IWaitlistSignup = sampleWithRequiredData;
        const waitlistSignup2: IWaitlistSignup = sampleWithPartialData;
        expectedResult = service.addWaitlistSignupToCollectionIfMissing([], waitlistSignup, waitlistSignup2);
        expect(expectedResult).toEqual([waitlistSignup, waitlistSignup2]);
      });

      it('should accept null and undefined values', () => {
        const waitlistSignup: IWaitlistSignup = sampleWithRequiredData;
        expectedResult = service.addWaitlistSignupToCollectionIfMissing([], null, waitlistSignup, undefined);
        expect(expectedResult).toEqual([waitlistSignup]);
      });

      it('should return initial array if no WaitlistSignup is added', () => {
        const waitlistSignupCollection: IWaitlistSignup[] = [sampleWithRequiredData];
        expectedResult = service.addWaitlistSignupToCollectionIfMissing(waitlistSignupCollection, undefined, null);
        expect(expectedResult).toEqual(waitlistSignupCollection);
      });
    });

    describe('compareWaitlistSignup', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWaitlistSignup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9304 };
        const entity2 = null;

        const compareResult1 = service.compareWaitlistSignup(entity1, entity2);
        const compareResult2 = service.compareWaitlistSignup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9304 };
        const entity2 = { id: 21600 };

        const compareResult1 = service.compareWaitlistSignup(entity1, entity2);
        const compareResult2 = service.compareWaitlistSignup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9304 };
        const entity2 = { id: 9304 };

        const compareResult1 = service.compareWaitlistSignup(entity1, entity2);
        const compareResult2 = service.compareWaitlistSignup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
