import dayjs from 'dayjs/esm';

import { ILaunchSetting, NewLaunchSetting } from './launch-setting.model';

export const sampleWithRequiredData: ILaunchSetting = {
  id: 19151,
  settingKey: 'hundred',
  organisationName: 'whether',
  launchAt: dayjs('2026-07-29T22:47'),
  launchTimezone: 'descriptive',
  fundUrl: 'warp',
  contactEmail: 'because',
  active: false,
};

export const sampleWithPartialData: ILaunchSetting = {
  id: 26435,
  settingKey: 'properly whereas',
  organisationName: 'yahoo orchestrate swelter',
  tagline: 'mash roger ha',
  launchAt: dayjs('2026-07-29T19:11'),
  launchTimezone: 'amused',
  fundUrl: 'gladly',
  contactEmail: 'perky hypothesise',
  contactPhone: 'loosely chase mysterious',
  officeAddress: 'pish ferret',
  active: true,
};

export const sampleWithFullData: ILaunchSetting = {
  id: 9763,
  settingKey: 'flawless shipper',
  organisationName: 'thorny',
  tagline: 'trench eek',
  launchAt: dayjs('2026-07-30T17:51'),
  launchTimezone: 'sophisticated though furthermore',
  fundUrl: 'bore',
  contactEmail: 'zowie juvenile',
  contactPhone: 'mixture oof drat',
  officeAddress: 'towards deprave similar',
  active: false,
};

export const sampleWithNewData: NewLaunchSetting = {
  settingKey: 'hyphenation',
  organisationName: 'ew slowly scornful',
  launchAt: dayjs('2026-07-30T00:51'),
  launchTimezone: 'yuppify bonnet colorize',
  fundUrl: 'busily funny',
  contactEmail: 'unless seemingly',
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
