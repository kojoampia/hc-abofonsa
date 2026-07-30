import dayjs from 'dayjs/esm';

import { IDataExportLog } from './data-export-log.model';

export const sampleWithRequiredData: IDataExportLog = {
  id: 19053,
  exportKind: 'METRIC_ROLLUPS',
  format: 'XLSX',
  rowCount: 2354,
  requestedBy: 'physically yahoo',
  requestedAt: dayjs('2026-07-30T00:19'),
};

export const sampleWithPartialData: IDataExportLog = {
  id: 14754,
  exportKind: 'WAITLIST_EMAILS',
  format: 'CSV',
  rangeFrom: dayjs('2026-07-29T21:00'),
  rangeTo: dayjs('2026-07-30T15:18'),
  rowCount: 13076,
  requestedBy: 'boo oh as',
  requestedAt: dayjs('2026-07-29T19:37'),
  durationMs: 10560,
};

export const sampleWithFullData: IDataExportLog = {
  id: 5765,
  exportKind: 'CAPTURE_EVENTS',
  format: 'CSV',
  rangeFrom: dayjs('2026-07-30T01:48'),
  rangeTo: dayjs('2026-07-30T15:03'),
  bucketType: 'MONTH',
  filterSummary: 'supposing aboard harvest',
  rowCount: 25046,
  requestedBy: 'incidentally lean',
  requestedAt: dayjs('2026-07-30T07:34'),
  durationMs: 12695,
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
