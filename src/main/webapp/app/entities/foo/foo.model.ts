import dayjs from 'dayjs/esm';
import { IDummy } from 'app/entities/dummy/dummy.model';

export interface IFoo {
  id: string;
  name?: string | null;
  description?: string | null;
  start?: dayjs.Dayjs | null;
  dummy?: Pick<IDummy, 'id'> | null;
}

export type NewFoo = Omit<IFoo, 'id'> & { id: null };
