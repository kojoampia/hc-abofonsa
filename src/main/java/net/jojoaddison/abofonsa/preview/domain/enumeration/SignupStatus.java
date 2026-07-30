package net.jojoaddison.abofonsa.preview.domain.enumeration;

/**
 * Lifecycle of a captured email. Double opt-in: PENDING until the link is clicked.
 */
public enum SignupStatus {
    PENDING,
    CONFIRMED,
    UNSUBSCRIBED,
    BOUNCED,
}
