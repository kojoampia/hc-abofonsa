import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILaunchSetting } from '../launch-setting.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../launch-setting.test-samples';

import { LaunchSettingService, RestLaunchSetting } from './launch-setting.service';

const requireRestSample: RestLaunchSetting = {
  ...sampleWithRequiredData,
  launchAt: sampleWithRequiredData.launchAt?.toJSON(),
};

describe('LaunchSetting Service', () => {
  let service: LaunchSettingService;
  let httpMock: HttpTestingController;
  let expectedResult: ILaunchSetting | ILaunchSetting[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LaunchSettingService);
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

    it('should create a LaunchSetting', () => {
      const launchSetting = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(launchSetting).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LaunchSetting', () => {
      const launchSetting = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(launchSetting).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LaunchSetting', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LaunchSetting', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LaunchSetting', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLaunchSettingToCollectionIfMissing', () => {
      it('should add a LaunchSetting to an empty array', () => {
        const launchSetting: ILaunchSetting = sampleWithRequiredData;
        expectedResult = service.addLaunchSettingToCollectionIfMissing([], launchSetting);
        expect(expectedResult).toEqual([launchSetting]);
      });

      it('should not add a LaunchSetting to an array that contains it', () => {
        const launchSetting: ILaunchSetting = sampleWithRequiredData;
        const launchSettingCollection: ILaunchSetting[] = [
          {
            ...launchSetting,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLaunchSettingToCollectionIfMissing(launchSettingCollection, launchSetting);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LaunchSetting to an array that doesn't contain it", () => {
        const launchSetting: ILaunchSetting = sampleWithRequiredData;
        const launchSettingCollection: ILaunchSetting[] = [sampleWithPartialData];
        expectedResult = service.addLaunchSettingToCollectionIfMissing(launchSettingCollection, launchSetting);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(launchSetting);
      });

      it('should add only unique LaunchSetting to an array', () => {
        const launchSettingArray: ILaunchSetting[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const launchSettingCollection: ILaunchSetting[] = [sampleWithRequiredData];
        expectedResult = service.addLaunchSettingToCollectionIfMissing(launchSettingCollection, ...launchSettingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const launchSetting: ILaunchSetting = sampleWithRequiredData;
        const launchSetting2: ILaunchSetting = sampleWithPartialData;
        expectedResult = service.addLaunchSettingToCollectionIfMissing([], launchSetting, launchSetting2);
        expect(expectedResult).toEqual([launchSetting, launchSetting2]);
      });

      it('should accept null and undefined values', () => {
        const launchSetting: ILaunchSetting = sampleWithRequiredData;
        expectedResult = service.addLaunchSettingToCollectionIfMissing([], null, launchSetting, undefined);
        expect(expectedResult).toEqual([launchSetting]);
      });

      it('should return initial array if no LaunchSetting is added', () => {
        const launchSettingCollection: ILaunchSetting[] = [sampleWithRequiredData];
        expectedResult = service.addLaunchSettingToCollectionIfMissing(launchSettingCollection, undefined, null);
        expect(expectedResult).toEqual(launchSettingCollection);
      });
    });

    describe('compareLaunchSetting', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLaunchSetting(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31747 };
        const entity2 = null;

        const compareResult1 = service.compareLaunchSetting(entity1, entity2);
        const compareResult2 = service.compareLaunchSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31747 };
        const entity2 = { id: 22879 };

        const compareResult1 = service.compareLaunchSetting(entity1, entity2);
        const compareResult2 = service.compareLaunchSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31747 };
        const entity2 = { id: 31747 };

        const compareResult1 = service.compareLaunchSetting(entity1, entity2);
        const compareResult2 = service.compareLaunchSetting(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
