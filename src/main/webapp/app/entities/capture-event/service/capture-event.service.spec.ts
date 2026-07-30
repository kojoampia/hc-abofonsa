import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICaptureEvent } from '../capture-event.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../capture-event.test-samples';

import { CaptureEventService, RestCaptureEvent } from './capture-event.service';

const requireRestSample: RestCaptureEvent = {
  ...sampleWithRequiredData,
  occurredAt: sampleWithRequiredData.occurredAt?.toJSON(),
  occurredDate: sampleWithRequiredData.occurredDate?.format(DATE_FORMAT),
};

describe('CaptureEvent Service', () => {
  let service: CaptureEventService;
  let httpMock: HttpTestingController;
  let expectedResult: ICaptureEvent | ICaptureEvent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CaptureEventService);
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

    it('should return a list of CaptureEvent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addCaptureEventToCollectionIfMissing', () => {
      it('should add a CaptureEvent to an empty array', () => {
        const captureEvent: ICaptureEvent = sampleWithRequiredData;
        expectedResult = service.addCaptureEventToCollectionIfMissing([], captureEvent);
        expect(expectedResult).toEqual([captureEvent]);
      });

      it('should not add a CaptureEvent to an array that contains it', () => {
        const captureEvent: ICaptureEvent = sampleWithRequiredData;
        const captureEventCollection: ICaptureEvent[] = [
          {
            ...captureEvent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCaptureEventToCollectionIfMissing(captureEventCollection, captureEvent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CaptureEvent to an array that doesn't contain it", () => {
        const captureEvent: ICaptureEvent = sampleWithRequiredData;
        const captureEventCollection: ICaptureEvent[] = [sampleWithPartialData];
        expectedResult = service.addCaptureEventToCollectionIfMissing(captureEventCollection, captureEvent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(captureEvent);
      });

      it('should add only unique CaptureEvent to an array', () => {
        const captureEventArray: ICaptureEvent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const captureEventCollection: ICaptureEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCaptureEventToCollectionIfMissing(captureEventCollection, ...captureEventArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const captureEvent: ICaptureEvent = sampleWithRequiredData;
        const captureEvent2: ICaptureEvent = sampleWithPartialData;
        expectedResult = service.addCaptureEventToCollectionIfMissing([], captureEvent, captureEvent2);
        expect(expectedResult).toEqual([captureEvent, captureEvent2]);
      });

      it('should accept null and undefined values', () => {
        const captureEvent: ICaptureEvent = sampleWithRequiredData;
        expectedResult = service.addCaptureEventToCollectionIfMissing([], null, captureEvent, undefined);
        expect(expectedResult).toEqual([captureEvent]);
      });

      it('should return initial array if no CaptureEvent is added', () => {
        const captureEventCollection: ICaptureEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCaptureEventToCollectionIfMissing(captureEventCollection, undefined, null);
        expect(expectedResult).toEqual(captureEventCollection);
      });
    });

    describe('compareCaptureEvent', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCaptureEvent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12764 };
        const entity2 = null;

        const compareResult1 = service.compareCaptureEvent(entity1, entity2);
        const compareResult2 = service.compareCaptureEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12764 };
        const entity2 = { id: 703 };

        const compareResult1 = service.compareCaptureEvent(entity1, entity2);
        const compareResult2 = service.compareCaptureEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12764 };
        const entity2 = { id: 12764 };

        const compareResult1 = service.compareCaptureEvent(entity1, entity2);
        const compareResult2 = service.compareCaptureEvent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
