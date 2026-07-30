import { ICareServiceTeaser, NewCareServiceTeaser } from './care-service-teaser.model';

export const sampleWithRequiredData: ICareServiceTeaser = {
  id: 8750,
  slug: 'within before sonnet',
  name: 'what although per',
  blurb: '../fake-data/blob/hipster.txt',
  displayOrder: 13631,
  published: false,
};

export const sampleWithPartialData: ICareServiceTeaser = {
  id: 13265,
  slug: 'um oh parody',
  name: 'receptor',
  blurb: '../fake-data/blob/hipster.txt',
  displayOrder: 22775,
  published: false,
};

export const sampleWithFullData: ICareServiceTeaser = {
  id: 20244,
  slug: 'without',
  name: 'optimal versus',
  blurb: '../fake-data/blob/hipster.txt',
  iconKey: 'justly',
  availableOn: 'torn premium',
  displayOrder: 14601,
  published: true,
};

export const sampleWithNewData: NewCareServiceTeaser = {
  slug: 'term among',
  name: 'gee emergent ack',
  blurb: '../fake-data/blob/hipster.txt',
  displayOrder: 30994,
  published: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
