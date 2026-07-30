import { ICarePlanTeaser } from 'app/entities/care-plan-teaser/care-plan-teaser.model';

export interface IPlanFeature {
  id: number;
  label?: string | null;
  included?: boolean | null;
  emphasised?: boolean | null;
  displayOrder?: number | null;
  plan?: Pick<ICarePlanTeaser, 'id' | 'name'> | null;
}

export type NewPlanFeature = Omit<IPlanFeature, 'id'> & { id: null };
