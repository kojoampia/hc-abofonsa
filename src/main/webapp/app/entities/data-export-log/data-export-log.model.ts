import dayjs from 'dayjs/esm';

import { BucketType } from 'app/entities/enumerations/bucket-type.model';
import { ExportFormat } from 'app/entities/enumerations/export-format.model';
import { ExportKind } from 'app/entities/enumerations/export-kind.model';

export interface IDataExportLog {
  id: number;
  exportKind?: keyof typeof ExportKind | null;
  format?: keyof typeof ExportFormat | null;
  rangeFrom?: dayjs.Dayjs | null;
  rangeTo?: dayjs.Dayjs | null;
  bucketType?: keyof typeof BucketType | null;
  filterSummary?: string | null;
  rowCount?: number | null;
  requestedBy?: string | null;
  requestedAt?: dayjs.Dayjs | null;
  durationMs?: number | null;
}
