import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDataExportLog } from '../data-export-log.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../data-export-log.test-samples';

import { DataExportLogService, RestDataExportLog } from './data-export-log.service';

const requireRestSample: RestDataExportLog = {
  ...sampleWithRequiredData,
  rangeFrom: sampleWithRequiredData.rangeFrom?.toJSON(),
  rangeTo: sampleWithRequiredData.rangeTo?.toJSON(),
  requestedAt: sampleWithRequiredData.requestedAt?.toJSON(),
};

describe('DataExportLog Service', () => {
  let service: DataExportLogService;
  let httpMock: HttpTestingController;
  let expectedResult: IDataExportLog | IDataExportLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DataExportLogService);
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

    it('should return a list of DataExportLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addDataExportLogToCollectionIfMissing', () => {
      it('should add a DataExportLog to an empty array', () => {
        const dataExportLog: IDataExportLog = sampleWithRequiredData;
        expectedResult = service.addDataExportLogToCollectionIfMissing([], dataExportLog);
        expect(expectedResult).toEqual([dataExportLog]);
      });

      it('should not add a DataExportLog to an array that contains it', () => {
        const dataExportLog: IDataExportLog = sampleWithRequiredData;
        const dataExportLogCollection: IDataExportLog[] = [
          {
            ...dataExportLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDataExportLogToCollectionIfMissing(dataExportLogCollection, dataExportLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DataExportLog to an array that doesn't contain it", () => {
        const dataExportLog: IDataExportLog = sampleWithRequiredData;
        const dataExportLogCollection: IDataExportLog[] = [sampleWithPartialData];
        expectedResult = service.addDataExportLogToCollectionIfMissing(dataExportLogCollection, dataExportLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataExportLog);
      });

      it('should add only unique DataExportLog to an array', () => {
        const dataExportLogArray: IDataExportLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dataExportLogCollection: IDataExportLog[] = [sampleWithRequiredData];
        expectedResult = service.addDataExportLogToCollectionIfMissing(dataExportLogCollection, ...dataExportLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dataExportLog: IDataExportLog = sampleWithRequiredData;
        const dataExportLog2: IDataExportLog = sampleWithPartialData;
        expectedResult = service.addDataExportLogToCollectionIfMissing([], dataExportLog, dataExportLog2);
        expect(expectedResult).toEqual([dataExportLog, dataExportLog2]);
      });

      it('should accept null and undefined values', () => {
        const dataExportLog: IDataExportLog = sampleWithRequiredData;
        expectedResult = service.addDataExportLogToCollectionIfMissing([], null, dataExportLog, undefined);
        expect(expectedResult).toEqual([dataExportLog]);
      });

      it('should return initial array if no DataExportLog is added', () => {
        const dataExportLogCollection: IDataExportLog[] = [sampleWithRequiredData];
        expectedResult = service.addDataExportLogToCollectionIfMissing(dataExportLogCollection, undefined, null);
        expect(expectedResult).toEqual(dataExportLogCollection);
      });
    });

    describe('compareDataExportLog', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDataExportLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16092 };
        const entity2 = null;

        const compareResult1 = service.compareDataExportLog(entity1, entity2);
        const compareResult2 = service.compareDataExportLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16092 };
        const entity2 = { id: 24644 };

        const compareResult1 = service.compareDataExportLog(entity1, entity2);
        const compareResult2 = service.compareDataExportLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16092 };
        const entity2 = { id: 16092 };

        const compareResult1 = service.compareDataExportLog(entity1, entity2);
        const compareResult2 = service.compareDataExportLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
