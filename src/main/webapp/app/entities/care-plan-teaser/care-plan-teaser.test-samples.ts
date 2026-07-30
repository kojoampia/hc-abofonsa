import { ICarePlanTeaser, NewCarePlanTeaser } from './care-plan-teaser.model';

export const sampleWithRequiredData: ICarePlanTeaser = {
  id: 15524,
  code: 'PEAR',
  name: 'till considering',
  forWho: '../fake-data/blob/hipster.txt',
  priceAmount: 26103.96,
  priceCurrency: 'lik',
  pricePeriod: 'perfectly amongst',
  featured: true,
  displayOrder: 3888,
  published: false,
};

export const sampleWithPartialData: ICarePlanTeaser = {
  id: 6580,
  code: 'MELON',
  name: 'geez boohoo welcome',
  forWho: '../fake-data/blob/hipster.txt',
  priceAmount: 28193.76,
  priceCurrency: 'wic',
  pricePeriod: 'banish but',
  priceNote: 'considering than ha',
  featured: true,
  displayOrder: 30847,
  published: false,
};

export const sampleWithFullData: ICarePlanTeaser = {
  id: 11405,
  code: 'PAWPAW',
  name: 'content uh-huh sting',
  forWho: '../fake-data/blob/hipster.txt',
  priceAmount: 18486.85,
  priceCurrency: 'bin',
  pricePeriod: 'readmit however',
  priceNote: 'peninsula where',
  featured: false,
  displayOrder: 11605,
  published: true,
};

// `code` is filled in by hand. generator-jhipster could not produce a value for it — the field is a
// required *unique* enum with only three members, so its unique-value faker has nothing left to
// generate and emits `undefined`, which then fails the form-service round-trip assertion. The exact
// value is arbitrary: this sample feeds a unit test that never reaches a database, so the unique
// constraint it was blocked by does not apply here.
export const sampleWithNewData: NewCarePlanTeaser = {
  code: 'PEAR',
  name: 'till',
  forWho: '../fake-data/blob/hipster.txt',
  priceAmount: 24443.47,
  priceCurrency: 'unb',
  pricePeriod: 'engender',
  featured: false,
  displayOrder: 29690,
  published: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
