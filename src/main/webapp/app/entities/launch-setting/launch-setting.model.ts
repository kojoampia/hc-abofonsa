import dayjs from 'dayjs/esm';

export interface ILaunchSetting {
  id: number;
  settingKey?: string | null;
  organisationName?: string | null;
  tagline?: string | null;
  launchAt?: dayjs.Dayjs | null;
  launchTimezone?: string | null;
  fundUrl?: string | null;
  contactEmail?: string | null;
  contactPhone?: string | null;
  officeAddress?: string | null;
  active?: boolean | null;
}

export type NewLaunchSetting = Omit<ILaunchSetting, 'id'> & { id: null };
