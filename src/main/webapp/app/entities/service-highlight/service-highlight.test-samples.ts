import { IServiceHighlight, NewServiceHighlight } from './service-highlight.model';

export const sampleWithRequiredData: IServiceHighlight = {
  id: 10344,
  label: 'procrastinate along whoa',
  displayOrder: 13115,
};

export const sampleWithPartialData: IServiceHighlight = {
  id: 30284,
  label: 'connect toady appertain',
  displayOrder: 23738,
};

export const sampleWithFullData: IServiceHighlight = {
  id: 8557,
  label: 'internal amongst',
  displayOrder: 19462,
};

export const sampleWithNewData: NewServiceHighlight = {
  label: 'ack ick',
  displayOrder: 32422,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
