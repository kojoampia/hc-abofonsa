package net.jojoaddison.abofonsa.preview.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import net.jojoaddison.abofonsa.preview.repository.*;
import net.jojoaddison.abofonsa.preview.service.dto.PublicContentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assembles the launch page's payload.
 *
 * <p>Cached, because the content changes when somebody edits a seed row and not otherwise, while
 * the page it feeds is the one every visitor hits first. {@link #evictContent()} is the release
 * valve for the admin CRUD screens.
 */
@Service
@Transactional(readOnly = true)
public class PublicContentService {

    public static final String CONTENT_CACHE = "publicContent";
    private static final String DEFAULT_SETTING_KEY = "DEFAULT";

    private static final Logger LOG = LoggerFactory.getLogger(PublicContentService.class);

    private final LaunchSettingRepository launchSettingRepository;
    private final LaunchMilestoneRepository launchMilestoneRepository;
    private final CareServiceTeaserRepository careServiceTeaserRepository;
    private final CarePlanTeaserRepository carePlanTeaserRepository;
    private final PledgeTierTeaserRepository pledgeTierTeaserRepository;
    private final SocialLinkRepository socialLinkRepository;

    /**
     * Where the browser posts its spans. Same-origin, proxied by nginx to the collector's browser
     * receiver — never the collector's own address, which is loopback-only and would not resolve
     * from a browser anyway.
     */
    private static final String RUM_ENDPOINT = "/v1/traces";

    private final boolean rumEnabled;
    private final double rumSampleRatio;

    public PublicContentService(
        LaunchSettingRepository launchSettingRepository,
        LaunchMilestoneRepository launchMilestoneRepository,
        CareServiceTeaserRepository careServiceTeaserRepository,
        CarePlanTeaserRepository carePlanTeaserRepository,
        PledgeTierTeaserRepository pledgeTierTeaserRepository,
        SocialLinkRepository socialLinkRepository,
        @Value("${abofonsa.rum.enabled:true}") boolean rumEnabled,
        @Value("${abofonsa.rum.sample-ratio:0.25}") double rumSampleRatio
    ) {
        this.launchSettingRepository = launchSettingRepository;
        this.launchMilestoneRepository = launchMilestoneRepository;
        this.careServiceTeaserRepository = careServiceTeaserRepository;
        this.carePlanTeaserRepository = carePlanTeaserRepository;
        this.pledgeTierTeaserRepository = pledgeTierTeaserRepository;
        this.socialLinkRepository = socialLinkRepository;
        this.rumEnabled = rumEnabled;
        this.rumSampleRatio = rumSampleRatio;
    }

    @Cacheable(CONTENT_CACHE)
    public PublicContentDTO getContent() {
        LOG.debug("Assembling public content payload (cache miss)");

        LaunchSetting setting = launchSettingRepository.findBySettingKeyAndActiveIsTrue(DEFAULT_SETTING_KEY).orElseThrow(() ->
            // Not a 404: the row is seeded by Liquibase, so its absence means the database was
            // not migrated rather than that the caller asked for something that does not exist.
            new IllegalStateException("No active LaunchSetting with key " + DEFAULT_SETTING_KEY + " — check the Liquibase seed")
        );

        return new PublicContentDTO(
            Instant.now(),
            new PublicContentDTO.Launch(
                setting.getOrganisationName(),
                setting.getTagline(),
                setting.getLaunchAt(),
                setting.getLaunchTimezone(),
                setting.getFundUrl(),
                setting.getContactEmail(),
                blankToNull(setting.getContactPhone()),
                setting.getOfficeAddress(),
                setting.getParentCompanyName(),
                setting.getParentCompanyUrl()
            ),
            milestones(),
            services(),
            plans(),
            pledgeTiers(),
            socialLinks(),
            new PublicContentDTO.Telemetry(rumEnabled, rumSampleRatio, RUM_ENDPOINT)
        );
    }

    /** Called by the admin CRUD layer whenever a content row changes. */
    @CacheEvict(value = CONTENT_CACHE, allEntries = true)
    public void evictContent() {
        LOG.debug("Evicted the public content cache");
    }

    private List<PublicContentDTO.Milestone> milestones() {
        return launchMilestoneRepository
            .findByPublishedIsTrueOrderByDisplayOrderAsc()
            .stream()
            .map(m ->
                new PublicContentDTO.Milestone(
                    m.getPhaseLabel(),
                    m.getTitle(),
                    m.getBody(),
                    m.getMilestoneDate(),
                    Boolean.TRUE.equals(m.getCurrent())
                )
            )
            .toList();
    }

    private List<PublicContentDTO.CareService> services() {
        return careServiceTeaserRepository
            .findPublishedWithHighlights()
            .stream()
            .map(s ->
                new PublicContentDTO.CareService(
                    s.getSlug(),
                    s.getName(),
                    s.getBlurb(),
                    s.getIconKey(),
                    s.getAvailableOn(),
                    // Sorted here rather than in the query: the association is a Set, so Hibernate
                    // discards any order the SQL imposed on it.
                    s
                        .getHighlights()
                        .stream()
                        .sorted(Comparator.comparing(h -> h.getDisplayOrder() == null ? Integer.MAX_VALUE : h.getDisplayOrder()))
                        .map(h -> h.getLabel())
                        .toList()
                )
            )
            .toList();
    }

    private List<PublicContentDTO.Plan> plans() {
        return carePlanTeaserRepository
            .findPublishedWithFeatures()
            .stream()
            .map(p ->
                new PublicContentDTO.Plan(
                    p.getCode() == null ? null : p.getCode().name(),
                    p.getName(),
                    p.getForWho(),
                    p.getPriceAmount(),
                    p.getPriceCurrency(),
                    p.getPricePeriod(),
                    p.getPriceNote(),
                    Boolean.TRUE.equals(p.getFeatured()),
                    p
                        .getFeatures()
                        .stream()
                        .sorted(Comparator.comparing(f -> f.getDisplayOrder() == null ? Integer.MAX_VALUE : f.getDisplayOrder()))
                        .map(f ->
                            new PublicContentDTO.Plan.Feature(
                                f.getLabel(),
                                Boolean.TRUE.equals(f.getIncluded()),
                                Boolean.TRUE.equals(f.getEmphasised())
                            )
                        )
                        .toList()
                )
            )
            .toList();
    }

    private List<PublicContentDTO.PledgeTier> pledgeTiers() {
        return pledgeTierTeaserRepository
            .findPublishedWithPerks()
            .stream()
            .map(t ->
                new PublicContentDTO.PledgeTier(
                    t.getCode() == null ? null : t.getCode().name(),
                    t.getName(),
                    t.getBlurb(),
                    t.getAmount(),
                    t.getCurrency(),
                    t.getVoucherValue(),
                    t.getHandoffUrl(),
                    t
                        .getPerks()
                        .stream()
                        .sorted(Comparator.comparing(p -> p.getDisplayOrder() == null ? Integer.MAX_VALUE : p.getDisplayOrder()))
                        .map(p -> p.getLabel())
                        .toList()
                )
            )
            .toList();
    }

    private List<PublicContentDTO.Social> socialLinks() {
        return socialLinkRepository
            .findByActiveIsTrueOrderByDisplayOrderAsc()
            .stream()
            .map(l ->
                new PublicContentDTO.Social(
                    l.getPlatform() == null ? null : l.getPlatform().name(),
                    l.getLabel(),
                    l.getUrl(),
                    l.getIconKey()
                )
            )
            .toList();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
