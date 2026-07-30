import dayjs from 'dayjs/esm';

export interface ILaunchMilestone {
  id: number;
  phaseLabel?: string | null;
  title?: string | null;
  body?: string | null;
  milestoneDate?: dayjs.Dayjs | null;
  current?: boolean | null;
  displayOrder?: number | null;
  published?: boolean | null;
}

export type NewLaunchMilestone = Omit<ILaunchMilestone, 'id'> & { id: null };
