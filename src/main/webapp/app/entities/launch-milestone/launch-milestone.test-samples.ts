import dayjs from 'dayjs/esm';

import { ILaunchMilestone, NewLaunchMilestone } from './launch-milestone.model';

export const sampleWithRequiredData: ILaunchMilestone = {
  id: 30932,
  phaseLabel: 'once while',
  title: 'wordy',
  current: false,
  displayOrder: 32540,
  published: true,
};

export const sampleWithPartialData: ILaunchMilestone = {
  id: 5757,
  phaseLabel: 'trash',
  title: 'puzzled singing slink',
  body: '../fake-data/blob/hipster.txt',
  milestoneDate: dayjs('2026-07-30'),
  current: true,
  displayOrder: 17136,
  published: false,
};

export const sampleWithFullData: ILaunchMilestone = {
  id: 13307,
  phaseLabel: 'duh wherever',
  title: 'sandy soliloquy',
  body: '../fake-data/blob/hipster.txt',
  milestoneDate: dayjs('2026-07-30'),
  current: true,
  displayOrder: 23515,
  published: false,
};

export const sampleWithNewData: NewLaunchMilestone = {
  phaseLabel: 'growing bitterly blah',
  title: 'outrun reluctantly',
  current: false,
  displayOrder: 17435,
  published: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
