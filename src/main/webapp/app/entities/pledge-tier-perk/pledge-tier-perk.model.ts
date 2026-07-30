import { IPledgeTierTeaser } from 'app/entities/pledge-tier-teaser/pledge-tier-teaser.model';

export interface IPledgeTierPerk {
  id: number;
  label?: string | null;
  displayOrder?: number | null;
  tier?: Pick<IPledgeTierTeaser, 'id' | 'name'> | null;
}

export type NewPledgeTierPerk = Omit<IPledgeTierPerk, 'id'> & { id: null };
