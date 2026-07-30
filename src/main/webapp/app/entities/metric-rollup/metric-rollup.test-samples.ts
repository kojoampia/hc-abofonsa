import dayjs from 'dayjs/esm';

import { IMetricRollup } from './metric-rollup.model';

export const sampleWithRequiredData: IMetricRollup = {
  id: 9785,
  metricKey: 'WAITLIST_CONFIRMED',
  bucketType: 'HOUR',
  bucketStart: dayjs('2026-07-30T16:20'),
  bucketEnd: dayjs('2026-07-30T04:47'),
  value: 14996,
  computedAt: dayjs('2026-07-30T08:30'),
};

export const sampleWithPartialData: IMetricRollup = {
  id: 505,
  metricKey: 'PAGE_VIEWS',
  bucketType: 'WEEK',
  bucketStart: dayjs('2026-07-30T02:28'),
  bucketEnd: dayjs('2026-07-30T12:27'),
  dimensionName: 'anenst eventually',
  value: 252,
  computedAt: dayjs('2026-07-30T03:09'),
};

export const sampleWithFullData: IMetricRollup = {
  id: 2863,
  metricKey: 'WAITLIST_UNSUBSCRIBED',
  bucketType: 'WEEK',
  bucketStart: dayjs('2026-07-30T14:38'),
  bucketEnd: dayjs('2026-07-30T14:19'),
  dimensionName: 'exterior thankfully',
  dimensionValue: 'whoever ramp',
  value: 10841,
  computedAt: dayjs('2026-07-30T11:37'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
