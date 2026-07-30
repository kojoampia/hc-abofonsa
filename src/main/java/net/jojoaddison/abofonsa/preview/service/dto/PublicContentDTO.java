package net.jojoaddison.abofonsa.preview.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Everything the launch page renders, in one response.
 *
 * <p>One request rather than six: the page is a single scroll with no navigation, so there is no
 * point at which a second payload would be more useful than a slightly larger first one, and the
 * countdown cannot start until the launch instant has arrived anyway.
 *
 * <p>These are deliberately not the generated entity DTOs. Those carry {@code id}, {@code
 * published} and the admin-facing audit columns; this is the public projection, and keeping it
 * separate means adding an internal field to an entity cannot silently publish it.
 */
public record PublicContentDTO(
    Instant generatedAt,
    Launch launch,
    List<Milestone> milestones,
    List<CareService> services,
    List<Plan> plans,
    List<PledgeTier> pledgeTiers,
    List<Social> socialLinks
) implements Serializable {
    /** Site identity and the countdown target. */
    public record Launch(
        String organisationName,
        String tagline,
        Instant launchAt,
        String launchTimezone,
        String fundUrl,
        String contactEmail,
        String contactPhone,
        String officeAddress
    ) implements Serializable {}

    public record Milestone(
        String phaseLabel,
        String title,
        String body,
        LocalDate milestoneDate,
        boolean current
    ) implements Serializable {}

    public record CareService(
        String slug,
        String name,
        String blurb,
        String iconKey,
        String availableOn,
        List<String> highlights
    ) implements Serializable {}

    public record Plan(
        String code,
        String name,
        String forWho,
        BigDecimal priceAmount,
        String priceCurrency,
        String pricePeriod,
        String priceNote,
        boolean featured,
        List<Feature> features
    ) implements Serializable {
        public record Feature(String label, boolean included, boolean emphasised) implements Serializable {}
    }

    /**
     * A tier as displayed. There is no pledge endpoint on this side — {@code handoffUrl} is where
     * the backer actually goes, and the click is counted on the way out.
     */
    public record PledgeTier(
        String code,
        String name,
        String blurb,
        BigDecimal amount,
        String currency,
        BigDecimal voucherValue,
        String handoffUrl,
        List<String> perks
    ) implements Serializable {}

    public record Social(String platform, String label, String url, String iconKey) implements Serializable {}
}
