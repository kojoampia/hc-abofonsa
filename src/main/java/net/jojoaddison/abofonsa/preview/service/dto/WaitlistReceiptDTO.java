package net.jojoaddison.abofonsa.preview.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * The answer to a waitlist submission.
 *
 * <p>{@code alreadyRegistered} is reported honestly rather than hidden. The usual argument for
 * pretending every submission is new is that the difference leaks whether an address is on the
 * list — but this list is a launch announcement, not an account, and telling somebody "you are
 * already on this list" is the useful answer. Nothing here reveals anything the person submitting
 * the address does not already know about their own address.
 */
public record WaitlistReceiptDTO(String reference, Status status, boolean alreadyRegistered, Instant receivedAt) implements Serializable {
    public enum Status {
        /** Captured; a confirmation link has been issued and awaits a click. */
        PENDING_CONFIRMATION,
        /** This address had already confirmed. Nothing changed. */
        ALREADY_CONFIRMED,
    }
}
