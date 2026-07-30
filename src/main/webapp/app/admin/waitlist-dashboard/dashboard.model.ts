/** Mirrors MetricSeriesDTO / MetricSummaryDTO on the server. */

export type MetricKey =
  | 'WAITLIST_SIGNUPS'
  | 'WAITLIST_CONFIRMED'
  | 'WAITLIST_UNSUBSCRIBED'
  | 'PLEDGE_CLICKS'
  | 'PAGE_VIEWS'
  | 'UNIQUE_VISITORS';

export type BucketType = 'HOUR' | 'DAY' | 'WEEK' | 'MONTH' | 'QUARTER' | 'YEAR';

export interface SeriesPoint {
  bucketStart: string;
  bucketEnd: string;
  value: number;
}

export interface Series {
  label: string;
  total: number;
  points: SeriesPoint[];
}

export interface MetricSeries {
  metricKey: MetricKey;
  bucketType: BucketType;
  from: string;
  to: string;
  dimensionName: string | null;
  /** The API clamped an over-wide request; the UI must say so rather than imply completeness. */
  truncated: boolean;
  series: Series[];
}

export interface SummaryTile {
  key: MetricKey;
  current: number;
  previous: number;
  /** null when the previous window was empty — there is no percentage change from nothing. */
  changePercent: number | null;
}

export interface MetricSummary {
  generatedAt: string;
  totalSignups: number;
  confirmedSignups: number;
  unsubscribed: number;
  tiles: SummaryTile[];
}

export interface WaitlistSignupRow {
  id: number;
  email: string;
  fullName: string | null;
  organisation: string | null;
  audience: string | null;
  planOfInterest: string | null;
  status: string;
  locale: string | null;
  utmSource: string | null;
  capturedAt: string;
  confirmedAt: string | null;
}
