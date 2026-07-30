package net.jojoaddison.abofonsa.preview.service.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import net.jojoaddison.abofonsa.preview.domain.enumeration.AudienceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;

/**
 * What the waitlist form posts.
 *
 * <p>Only {@code email} and {@code consent} are required. The design's form is a single input and a
 * button, and every field beyond the address is optional precisely so that adding a "tell us more"
 * step later does not become a breaking change.
 */
public record WaitlistSubmissionDTO(
    @NotBlank(message = "email is required") @Email(message = "email must be a valid email address") @Size(max = 254) String email,

    @Size(max = 120) String fullName,

    @Size(max = 160) String organisation,

    AudienceType audience,

    PlanCode planOfInterest,

    @Size(max = 10) String locale,

    @Size(max = 255) String sourcePage,

    @Size(max = 120) String utmSource,

    @Size(max = 120) String utmMedium,

    @Size(max = 120) String utmCampaign,

    /**
     * Consent to be contacted. Modelled as a required-true boolean rather than an optional flag: a
     * signup without it is not a weaker signup, it is one we are not allowed to act on.
     */
    @AssertTrue(message = "consent is required") boolean consent,

    /**
     * Honeypot. The real form renders this field hidden, so a human never fills it and any value at
     * all identifies a bot that filled the form by parsing the DOM.
     */
    @Size(max = 255) String company,

    /**
     * Milliseconds the form was on screen before submit. A human cannot read the page, type an
     * address and submit in under a second; a script routinely does it in tens of milliseconds.
     */
    long dwellMs
) implements Serializable {}
