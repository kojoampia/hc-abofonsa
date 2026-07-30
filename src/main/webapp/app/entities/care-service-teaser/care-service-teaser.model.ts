export interface ICareServiceTeaser {
  id: number;
  slug?: string | null;
  name?: string | null;
  blurb?: string | null;
  iconKey?: string | null;
  availableOn?: string | null;
  displayOrder?: number | null;
  published?: boolean | null;
}

export type NewCareServiceTeaser = Omit<ICareServiceTeaser, 'id'> & { id: null };
