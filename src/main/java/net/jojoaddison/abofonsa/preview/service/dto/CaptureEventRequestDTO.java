package net.jojoaddison.abofonsa.preview.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;

/**
 * The analytics beacon's payload.
 *
 * <p>Note what is absent: no user id, no cookie, no persistent identifier. The session hash is
 * derived server-side per visit, so the page cannot assert who it is and there is nothing here to
 * join across visits.
 *
 * <p>{@code WAITLIST_SUBMIT} and {@code WAITLIST_CONFIRM} are rejected by the endpoint even though
 * the enum allows them — those are recorded by the server when the thing actually happens, and
 * accepting them from the browser would let anyone inflate the signup count without signing up.
 */
public record CaptureEventRequestDTO(
    @NotNull(message = "eventType is required") CaptureEventType eventType,

    @Size(max = 10) String locale,

    @Size(max = 255) String sourcePage,

    @Size(max = 120) String utmSource,

    @Size(max = 120) String utmMedium,

    @Size(max = 120) String utmCampaign,

    /** A service slug, plan code, tier code or social platform — whatever was interacted with. */
    @Size(max = 120) String targetKey
) implements Serializable {}
