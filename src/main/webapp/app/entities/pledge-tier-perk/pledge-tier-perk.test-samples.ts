import { IPledgeTierPerk, NewPledgeTierPerk } from './pledge-tier-perk.model';

export const sampleWithRequiredData: IPledgeTierPerk = {
  id: 15572,
  label: 'suddenly on',
  displayOrder: 19036,
};

export const sampleWithPartialData: IPledgeTierPerk = {
  id: 17061,
  label: 'unaccountably furthermore',
  displayOrder: 3294,
};

export const sampleWithFullData: IPledgeTierPerk = {
  id: 2943,
  label: 'anenst steak huzzah',
  displayOrder: 6798,
};

export const sampleWithNewData: NewPledgeTierPerk = {
  label: 'ha unfreeze',
  displayOrder: 25634,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
