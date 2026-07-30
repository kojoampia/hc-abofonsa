import dayjs from 'dayjs/esm';

import { ICaptureEvent } from './capture-event.model';

export const sampleWithRequiredData: ICaptureEvent = {
  id: 17801,
  eventType: 'WAITLIST_SUBMIT',
  occurredAt: dayjs('2026-07-30T09:12'),
  occurredDate: dayjs('2026-07-30'),
};

export const sampleWithPartialData: ICaptureEvent = {
  id: 21457,
  eventType: 'PAGE_VIEW',
  occurredAt: dayjs('2026-07-29T23:18'),
  occurredDate: dayjs('2026-07-29'),
  utmMedium: 'milestone upliftingly',
  utmCampaign: 'provided pfft curse',
  referrerHost: 'sans',
  targetKey: 'needy',
};

export const sampleWithFullData: ICaptureEvent = {
  id: 21826,
  eventType: 'SERVICE_VIEW',
  occurredAt: dayjs('2026-07-30T17:13'),
  occurredDate: dayjs('2026-07-30'),
  sessionHash: 'severe abseil why',
  locale: 'early',
  sourcePage: 'drat scrutinise',
  utmSource: 'depute',
  utmMedium: 'detective minus',
  utmCampaign: 'bitterly what fraudster',
  referrerHost: 'priesthood toward',
  deviceType: 'BOT',
  countryCode: 'IT',
  targetKey: 'angrily',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
