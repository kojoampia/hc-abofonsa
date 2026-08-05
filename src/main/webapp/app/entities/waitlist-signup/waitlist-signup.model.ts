import dayjs from 'dayjs/esm';

import { AudienceType } from 'app/entities/enumerations/audience-type.model';
import { DeviceType } from 'app/entities/enumerations/device-type.model';
import { PlanCode } from 'app/entities/enumerations/plan-code.model';
import { SignupStatus } from 'app/entities/enumerations/signup-status.model';

/**
 * Note what is absent: `confirmationToken`, `unsubscribeToken`, `confirmationExpiresAt`, `ipHash`
 * and `userAgent`. They exist on the entity and are deliberately not on the DTO — the first three
 * are credentials for the opt-in and unsubscribe links, and the last two are personal data with no
 * reason to reach an admin screen. Re-running the entity generator will put them back.
 */
export interface IWaitlistSignup {
  id: number;
  email?: string | null;
  emailNormalized?: string | null;
  fullName?: string | null;
  organisation?: string | null;
  audience?: keyof typeof AudienceType | null;
  planOfInterest?: keyof typeof PlanCode | null;
  status?: keyof typeof SignupStatus | null;
  locale?: string | null;
  sourcePage?: string | null;
  utmSource?: string | null;
  utmMedium?: string | null;
  utmCampaign?: string | null;
  referrer?: string | null;
  deviceType?: keyof typeof DeviceType | null;
  consentGiven?: boolean | null;
  confirmedAt?: dayjs.Dayjs | null;
  unsubscribedAt?: dayjs.Dayjs | null;
  capturedAt?: dayjs.Dayjs | null;
}

export type NewWaitlistSignup = Omit<IWaitlistSignup, 'id'> & { id: null };
