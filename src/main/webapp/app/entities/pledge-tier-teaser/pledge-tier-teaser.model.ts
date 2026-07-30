import { PledgeTierCode } from 'app/entities/enumerations/pledge-tier-code.model';

export interface IPledgeTierTeaser {
  id: number;
  code?: keyof typeof PledgeTierCode | null;
  name?: string | null;
  blurb?: string | null;
  amount?: number | null;
  currency?: string | null;
  voucherValue?: number | null;
  handoffUrl?: string | null;
  displayOrder?: number | null;
  published?: boolean | null;
}

export type NewPledgeTierTeaser = Omit<IPledgeTierTeaser, 'id'> & { id: null };
