import { ICareServiceTeaser } from 'app/entities/care-service-teaser/care-service-teaser.model';

export interface IServiceHighlight {
  id: number;
  label?: string | null;
  displayOrder?: number | null;
  service?: Pick<ICareServiceTeaser, 'id' | 'name'> | null;
}

export type NewServiceHighlight = Omit<IServiceHighlight, 'id'> & { id: null };
