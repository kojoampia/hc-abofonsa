import { SocialPlatform } from 'app/entities/enumerations/social-platform.model';

export interface ISocialLink {
  id: number;
  platform?: keyof typeof SocialPlatform | null;
  label?: string | null;
  url?: string | null;
  iconKey?: string | null;
  displayOrder?: number | null;
  active?: boolean | null;
}

export type NewSocialLink = Omit<ISocialLink, 'id'> & { id: null };
