package net.jojoaddison.abofonsa.preview.domain.enumeration;

/**
 * Every countable thing the public page does. PLEDGE_CTA_CLICK is how a hand-off to
 * fund.abofonsa.com stays measurable on this side of the redirect.
 */
public enum CaptureEventType {
    PAGE_VIEW,
    WAITLIST_SUBMIT,
    WAITLIST_CONFIRM,
    WAITLIST_DUPLICATE,
    PLEDGE_CTA_CLICK,
    SERVICE_VIEW,
    PLAN_VIEW,
    CONTACT_CLICK,
    SOCIAL_CLICK,
}
