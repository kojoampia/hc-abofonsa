import dayjs from 'dayjs/esm';

import { CaptureEventType } from 'app/entities/enumerations/capture-event-type.model';
import { DeviceType } from 'app/entities/enumerations/device-type.model';

export interface ICaptureEvent {
  id: number;
  eventType?: keyof typeof CaptureEventType | null;
  occurredAt?: dayjs.Dayjs | null;
  occurredDate?: dayjs.Dayjs | null;
  sessionHash?: string | null;
  locale?: string | null;
  sourcePage?: string | null;
  utmSource?: string | null;
  utmMedium?: string | null;
  utmCampaign?: string | null;
  referrerHost?: string | null;
  deviceType?: keyof typeof DeviceType | null;
  countryCode?: string | null;
  targetKey?: string | null;
}
