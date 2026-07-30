import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISocialLink } from '../social-link.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../social-link.test-samples';

import { SocialLinkService } from './social-link.service';

const requireRestSample: ISocialLink = {
  ...sampleWithRequiredData,
};

describe('SocialLink Service', () => {
  let service: SocialLinkService;
  let httpMock: HttpTestingController;
  let expectedResult: ISocialLink | ISocialLink[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SocialLinkService);
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

    it('should create a SocialLink', () => {
      const socialLink = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(socialLink).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SocialLink', () => {
      const socialLink = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(socialLink).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SocialLink', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SocialLink', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SocialLink', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addSocialLinkToCollectionIfMissing', () => {
      it('should add a SocialLink to an empty array', () => {
        const socialLink: ISocialLink = sampleWithRequiredData;
        expectedResult = service.addSocialLinkToCollectionIfMissing([], socialLink);
        expect(expectedResult).toEqual([socialLink]);
      });

      it('should not add a SocialLink to an array that contains it', () => {
        const socialLink: ISocialLink = sampleWithRequiredData;
        const socialLinkCollection: ISocialLink[] = [
          {
            ...socialLink,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSocialLinkToCollectionIfMissing(socialLinkCollection, socialLink);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SocialLink to an array that doesn't contain it", () => {
        const socialLink: ISocialLink = sampleWithRequiredData;
        const socialLinkCollection: ISocialLink[] = [sampleWithPartialData];
        expectedResult = service.addSocialLinkToCollectionIfMissing(socialLinkCollection, socialLink);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(socialLink);
      });

      it('should add only unique SocialLink to an array', () => {
        const socialLinkArray: ISocialLink[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const socialLinkCollection: ISocialLink[] = [sampleWithRequiredData];
        expectedResult = service.addSocialLinkToCollectionIfMissing(socialLinkCollection, ...socialLinkArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const socialLink: ISocialLink = sampleWithRequiredData;
        const socialLink2: ISocialLink = sampleWithPartialData;
        expectedResult = service.addSocialLinkToCollectionIfMissing([], socialLink, socialLink2);
        expect(expectedResult).toEqual([socialLink, socialLink2]);
      });

      it('should accept null and undefined values', () => {
        const socialLink: ISocialLink = sampleWithRequiredData;
        expectedResult = service.addSocialLinkToCollectionIfMissing([], null, socialLink, undefined);
        expect(expectedResult).toEqual([socialLink]);
      });

      it('should return initial array if no SocialLink is added', () => {
        const socialLinkCollection: ISocialLink[] = [sampleWithRequiredData];
        expectedResult = service.addSocialLinkToCollectionIfMissing(socialLinkCollection, undefined, null);
        expect(expectedResult).toEqual(socialLinkCollection);
      });
    });

    describe('compareSocialLink', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSocialLink(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18647 };
        const entity2 = null;

        const compareResult1 = service.compareSocialLink(entity1, entity2);
        const compareResult2 = service.compareSocialLink(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18647 };
        const entity2 = { id: 24820 };

        const compareResult1 = service.compareSocialLink(entity1, entity2);
        const compareResult2 = service.compareSocialLink(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18647 };
        const entity2 = { id: 18647 };

        const compareResult1 = service.compareSocialLink(entity1, entity2);
        const compareResult2 = service.compareSocialLink(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
