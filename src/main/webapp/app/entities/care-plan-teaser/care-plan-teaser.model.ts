import { PlanCode } from 'app/entities/enumerations/plan-code.model';

export interface ICarePlanTeaser {
  id: number;
  code?: keyof typeof PlanCode | null;
  name?: string | null;
  forWho?: string | null;
  priceAmount?: number | null;
  priceCurrency?: string | null;
  pricePeriod?: string | null;
  priceNote?: string | null;
  featured?: boolean | null;
  displayOrder?: number | null;
  published?: boolean | null;
}

export type NewCarePlanTeaser = Omit<ICarePlanTeaser, 'id'> & { id: null };
