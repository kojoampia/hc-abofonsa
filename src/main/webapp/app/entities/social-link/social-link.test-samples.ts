import { ISocialLink, NewSocialLink } from './social-link.model';

export const sampleWithRequiredData: ISocialLink = {
  id: 15997,
  platform: 'WEBSITE',
  label: 'meh',
  url: 'https://soulful-mouser.name',
  displayOrder: 24079,
  active: false,
};

export const sampleWithPartialData: ISocialLink = {
  id: 7008,
  platform: 'EMAIL',
  label: 'who notwithstanding',
  url: 'https://fussy-circumference.name',
  iconKey: 'how plus shudder',
  displayOrder: 13420,
  active: false,
};

export const sampleWithFullData: ISocialLink = {
  id: 22177,
  platform: 'INSTAGRAM',
  label: 'immediately good',
  url: 'https://yearly-range.info/',
  iconKey: 'yahoo sleepily reboot',
  displayOrder: 12527,
  active: true,
};

export const sampleWithNewData: NewSocialLink = {
  platform: 'INSTAGRAM',
  label: 'magnetize hole',
  url: 'https://imaginative-meadow.org',
  displayOrder: 27425,
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
