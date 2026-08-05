/**
 * Mirrors PublicContentDTO on the server. Kept by hand rather than generated because it is a
 * hand-written endpoint, and small enough that a code generator would cost more than it saves.
 */

export interface LaunchSettings {
  organisationName: string;
  tagline: string | null;
  /** ISO instant. The countdown ticks towards this, never a constant baked into the bundle. */
  launchAt: string;
  launchTimezone: string;
  fundUrl: string;
  contactEmail: string;
  contactPhone: string | null;
  officeAddress: string | null;
  /** The consultancy that owns the product. Shown at the top and bottom of the page. */
  parentCompanyName: string;
  parentCompanyUrl: string;
}

export interface Milestone {
  phaseLabel: string;
  title: string;
  body: string | null;
  milestoneDate: string | null;
  current: boolean;
}

export interface CareService {
  slug: string;
  name: string;
  blurb: string;
  iconKey: string | null;
  availableOn: string | null;
  highlights: string[];
}

export interface PlanFeature {
  label: string;
  included: boolean;
  emphasised: boolean;
}

export interface Plan {
  code: string;
  name: string;
  forWho: string;
  priceAmount: number;
  priceCurrency: string;
  pricePeriod: string;
  priceNote: string | null;
  featured: boolean;
  features: PlanFeature[];
}

export interface PledgeTier {
  code: string;
  name: string;
  blurb: string | null;
  amount: number;
  currency: string;
  voucherValue: number | null;
  /** Deep link into fund.abofonsa.com. This app displays the offer; that platform takes the money. */
  handoffUrl: string;
  perks: string[];
}

export interface SocialLink {
  platform: string;
  label: string;
  url: string;
  iconKey: string | null;
}

export interface LaunchContent {
  generatedAt: string;
  launch: LaunchSettings;
  milestones: Milestone[];
  services: CareService[];
  plans: Plan[];
  pledgeTiers: PledgeTier[];
  socialLinks: SocialLink[];
}

export type CaptureEventType = 'PAGE_VIEW' | 'PLEDGE_CTA_CLICK' | 'SERVICE_VIEW' | 'PLAN_VIEW' | 'CONTACT_CLICK' | 'SOCIAL_CLICK';

export interface CaptureEventRequest {
  eventType: CaptureEventType;
  locale?: string;
  sourcePage?: string;
  utmSource?: string | null;
  utmMedium?: string | null;
  utmCampaign?: string | null;
  targetKey?: string | null;
}

export interface WaitlistSubmission {
  email: string;
  fullName?: string | null;
  organisation?: string | null;
  audience?: string | null;
  planOfInterest?: string | null;
  locale?: string;
  sourcePage?: string;
  utmSource?: string | null;
  utmMedium?: string | null;
  utmCampaign?: string | null;
  consent: boolean;
  /** Honeypot. Rendered hidden, so anything in it came from a bot parsing the DOM. */
  company?: string;
  /** How long the form was on screen. The server rejects implausibly fast submissions. */
  dwellMs: number;
}

export interface WaitlistReceipt {
  reference: string | null;
  status: 'PENDING_CONFIRMATION' | 'ALREADY_CONFIRMED';
  alreadyRegistered: boolean;
  receivedAt: string;
}

/**
 * The outcome of confirming or unsubscribing.
 *
 * `ok` or `invalid`, and nothing more granular: "no such token", "already used" and "expired" are
 * each a fact about somebody else's subscription, and distinguishing them would turn the endpoint
 * into a way to test whether a given address is on the list.
 */
export interface OptInResult {
  status: 'ok' | 'invalid';
}
