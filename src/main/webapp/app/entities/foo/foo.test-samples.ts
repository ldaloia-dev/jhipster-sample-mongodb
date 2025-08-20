import dayjs from 'dayjs/esm';

import { IFoo, NewFoo } from './foo.model';

export const sampleWithRequiredData: IFoo = {
  id: '7f6fd183-f1d0-422e-b16a-ee8064ab22ae',
  name: 'trusty',
};

export const sampleWithPartialData: IFoo = {
  id: '68aed610-9223-4fe4-8813-9df4c14154b9',
  name: 'stiff swerve besides',
  description: 'scent bootleg',
  start: dayjs('2025-08-20T04:44'),
};

export const sampleWithFullData: IFoo = {
  id: 'e6a72df5-594c-438a-ba76-bad188789b2e',
  name: 'striking uh-huh wing',
  description: 'because although',
  start: dayjs('2025-08-20T01:51'),
};

export const sampleWithNewData: NewFoo = {
  name: 'sweetly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
