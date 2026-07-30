import dayjs from 'dayjs/esm';

import { IWaitlistSignup, NewWaitlistSignup } from './waitlist-signup.model';

export const sampleWithRequiredData: IWaitlistSignup = {
  id: 26737,
  email: 'bKh|T@KBF`.1Pk+au',
  emailNormalized: 'zowie',
  status: 'PENDING',
  consentGiven: true,
  capturedAt: dayjs('2026-07-30T06:28'),
};

export const sampleWithPartialData: IWaitlistSignup = {
  id: 1611,
  email: "-J@//`nM'.KH.",
  emailNormalized: 'contrail vice',
  fullName: 'duster revere as',
  organisation: 'eek but daily',
  status: 'CONFIRMED',
  locale: 'rough',
  utmMedium: 'eek deer unhappy',
  utmCampaign: 'as gee oof',
  referrer: 'swat thorny poorly',
  deviceType: 'BOT',
  consentGiven: false,
  unsubscribedAt: dayjs('2026-07-30T16:44'),
  capturedAt: dayjs('2026-07-29T18:44'),
};

export const sampleWithFullData: IWaitlistSignup = {
  id: 18167,
  email: 'P<O@i.:',
  emailNormalized: 'suddenly however among',
  fullName: 'junior',
  organisation: 'deed and',
  audience: 'CLINIC',
  planOfInterest: 'MELON',
  status: 'BOUNCED',
  locale: 'now early ',
  sourcePage: 'who slide absent',
  utmSource: 'failing kookily sheepishly',
  utmMedium: 'blink',
  utmCampaign: 'atop pfft',
  referrer: 'resolve',
  deviceType: 'BOT',
  consentGiven: false,
  confirmationToken: 'whereas punctuation',
  confirmedAt: dayjs('2026-07-29T19:00'),
  unsubscribedAt: dayjs('2026-07-30T10:08'),
  capturedAt: dayjs('2026-07-30T15:08'),
  ipHash: 'blaspheme impact',
  userAgent: 'quaintly',
};

export const sampleWithNewData: NewWaitlistSignup = {
  email: 'BY@6.fC',
  emailNormalized: 'elderly qua unconscious',
  status: 'BOUNCED',
  consentGiven: false,
  capturedAt: dayjs('2026-07-30T01:28'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
