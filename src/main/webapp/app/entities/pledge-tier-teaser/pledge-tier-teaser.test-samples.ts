import { IPledgeTierTeaser, NewPledgeTierTeaser } from './pledge-tier-teaser.model';

export const sampleWithRequiredData: IPledgeTierTeaser = {
  id: 25981,
  code: 'BRONZE',
  name: 'bah because',
  amount: 13048.7,
  currency: 'mil',
  handoffUrl: 'husky like uselessly',
  displayOrder: 28103,
  published: false,
};

export const sampleWithPartialData: IPledgeTierTeaser = {
  id: 9434,
  code: 'SILVER',
  name: 'likewise ack',
  amount: 12993.07,
  currency: 'phe',
  voucherValue: 9406.68,
  handoffUrl: 'musty aha',
  displayOrder: 26387,
  published: false,
};

export const sampleWithFullData: IPledgeTierTeaser = {
  id: 22772,
  code: 'GOLD',
  name: 'shore',
  blurb: '../fake-data/blob/hipster.txt',
  amount: 29758.55,
  currency: 'tec',
  voucherValue: 28789.71,
  handoffUrl: 'wallaby annually mom',
  displayOrder: 5806,
  published: true,
};

export const sampleWithNewData: NewPledgeTierTeaser = {
  code: undefined,
  name: 'pillow',
  amount: 20942.06,
  currency: 'tha',
  handoffUrl: 'although',
  displayOrder: 24072,
  published: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
