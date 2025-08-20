export interface IDummy {
  id: string;
  name?: string | null;
  description?: string | null;
}

export type NewDummy = Omit<IDummy, 'id'> & { id: null };
