import { IPlanFeature, NewPlanFeature } from './plan-feature.model';

export const sampleWithRequiredData: IPlanFeature = {
  id: 26854,
  label: 'before',
  included: true,
  emphasised: false,
  displayOrder: 24405,
};

export const sampleWithPartialData: IPlanFeature = {
  id: 25939,
  label: 'downright agitated ugh',
  included: true,
  emphasised: true,
  displayOrder: 11815,
};

export const sampleWithFullData: IPlanFeature = {
  id: 27410,
  label: 'sugary',
  included: false,
  emphasised: false,
  displayOrder: 19623,
};

export const sampleWithNewData: NewPlanFeature = {
  label: 'hm machine maroon',
  included: true,
  emphasised: true,
  displayOrder: 1906,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
