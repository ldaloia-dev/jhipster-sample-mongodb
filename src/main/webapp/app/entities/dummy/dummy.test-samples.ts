import { IDummy, NewDummy } from './dummy.model';

export const sampleWithRequiredData: IDummy = {
  id: '61b42f96-c668-4276-a7a3-3e1435cb8372',
};

export const sampleWithPartialData: IDummy = {
  id: '589f2c24-75a1-40e6-8e3c-95893829bb12',
  description: 'sand',
};

export const sampleWithFullData: IDummy = {
  id: '7ffe38fb-543a-43a4-8b8c-90b558be5212',
  name: 'bowling and',
  description: 'discontinue netsuke',
};

export const sampleWithNewData: NewDummy = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
