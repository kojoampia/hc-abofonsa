import dayjs from 'dayjs/esm';

import { BucketType } from 'app/entities/enumerations/bucket-type.model';
import { MetricKey } from 'app/entities/enumerations/metric-key.model';

export interface IMetricRollup {
  id: number;
  metricKey?: keyof typeof MetricKey | null;
  bucketType?: keyof typeof BucketType | null;
  bucketStart?: dayjs.Dayjs | null;
  bucketEnd?: dayjs.Dayjs | null;
  dimensionName?: string | null;
  dimensionValue?: string | null;
  value?: number | null;
  computedAt?: dayjs.Dayjs | null;
}
