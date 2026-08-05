package net.jojoaddison.abofonsa.preview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.*;
import net.jojoaddison.abofonsa.preview.domain.enumeration.*;
import net.jojoaddison.abofonsa.preview.repository.*;
import net.jojoaddison.abofonsa.preview.service.PublicContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the unauthenticated launch-page API.
 *
 * <p>Deliberately not {@code @WithMockUser}: every one of these endpoints has to work for an
 * anonymous visitor, and running them as an authenticated principal would hide exactly the
 * regression most worth catching.
 *
 * <p>The fixtures are built here rather than read from the Liquibase seed. The content seeds carry
 * {@code contextFilter="!test"} so that JHipster's generated entity ITs get the empty tables they
 * assume — and a test that asserts on production seed rows is testing the migration, not the
 * endpoint, and breaks the moment somebody edits the copy.
 */
@IntegrationTest
@AutoConfigureMockMvc
class PublicApiResourceIT {

    private static final String CONTENT_URL = "/api/public/content";
    private static final String WAITLIST_URL = "/api/public/waitlist";
    private static final String EVENTS_URL = "/api/public/events";

    private static final Instant LAUNCH_AT = LocalDate.of(2027, 2, 1).atStartOfDay().toInstant(ZoneOffset.UTC);

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WaitlistSignupRepository waitlistSignupRepository;

    @Autowired
    private CaptureEventRepository captureEventRepository;

    @Autowired
    private LaunchSettingRepository launchSettingRepository;

    @Autowired
    private LaunchMilestoneRepository launchMilestoneRepository;

    @Autowired
    private CareServiceTeaserRepository careServiceTeaserRepository;

    @Autowired
    private ServiceHighlightRepository serviceHighlightRepository;

    @Autowired
    private CarePlanTeaserRepository carePlanTeaserRepository;

    @Autowired
    private PledgeTierTeaserRepository pledgeTierTeaserRepository;

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void seedFixtures() {
        // The payload is cached for the whole context, so a stale entry from another test would be
        // served instead of these fixtures.
        publicContentService.evictContent();

        serviceHighlightRepository.deleteAll();
        careServiceTeaserRepository.deleteAll();
        carePlanTeaserRepository.deleteAll();
        pledgeTierTeaserRepository.deleteAll();
        launchMilestoneRepository.deleteAll();
        socialLinkRepository.deleteAll();
        launchSettingRepository.deleteAll();
        waitlistSignupRepository.deleteAll();

        LaunchSetting setting = new LaunchSetting();
        setting.setSettingKey("DEFAULT");
        setting.setOrganisationName("Abofonsa BridgeCare");
        setting.setTagline("Decentralizing healthcare in Ghana.");
        setting.setLaunchAt(LAUNCH_AT);
        setting.setLaunchTimezone("GMT");
        setting.setFundUrl("https://fund.abofonsa.com");
        setting.setContactEmail("hello@abofonsa.com");
        setting.setParentCompanyName("Jojo Addison Consultancy");
        setting.setParentCompanyUrl("https://jojoaddison.net");
        setting.setActive(true);
        launchSettingRepository.saveAndFlush(setting);

        CareServiceTeaser service = new CareServiceTeaser();
        service.setSlug("skilled-nursing-visits");
        service.setName("Skilled nursing visits");
        service.setBlurb("Clinical care delivered at home by registered nurses.");
        service.setAvailableOn("All plans");
        service.setDisplayOrder(1);
        service.setPublished(true);
        careServiceTeaserRepository.saveAndFlush(service);

        // Saved out of display order on purpose — the endpoint has to sort them, and a fixture that
        // is already in order cannot prove that it does.
        highlight(service, "Chronic condition management", 2);
        highlight(service, "Vital signs, wound care and dressings", 1);

        CareServiceTeaser unpublished = new CareServiceTeaser();
        unpublished.setSlug("not-ready-yet");
        unpublished.setName("Draft service");
        unpublished.setBlurb("Should never reach the public payload.");
        unpublished.setDisplayOrder(2);
        unpublished.setPublished(false);
        careServiceTeaserRepository.saveAndFlush(unpublished);

        CarePlanTeaser plan = new CarePlanTeaser();
        plan.setCode(PlanCode.PAWPAW);
        plan.setName("PAWPAW Plan");
        plan.setForWho("Daily clinical oversight.");
        plan.setPriceAmount(new BigDecimal("5000.00"));
        plan.setPriceCurrency("GHS");
        plan.setPricePeriod("MONTH");
        plan.setFeatured(true);
        plan.setDisplayOrder(1);
        plan.setPublished(true);
        carePlanTeaserRepository.saveAndFlush(plan);

        PledgeTierTeaser tier = new PledgeTierTeaser();
        tier.setCode(PledgeTierCode.GOLD);
        tier.setName("Gold Angel");
        tier.setAmount(new BigDecimal("50000.00"));
        tier.setCurrency("GHS");
        tier.setVoucherValue(new BigDecimal("150000.00"));
        tier.setHandoffUrl("https://fund.abofonsa.com/pledge?tier=GOLD");
        tier.setDisplayOrder(1);
        tier.setPublished(true);
        pledgeTierTeaserRepository.saveAndFlush(tier);

        LaunchMilestone milestone = new LaunchMilestone();
        milestone.setPhaseLabel("Now");
        milestone.setTitle("Closed pilot");
        milestone.setCurrent(true);
        milestone.setDisplayOrder(1);
        milestone.setPublished(true);
        launchMilestoneRepository.saveAndFlush(milestone);

        socialLink(SocialPlatform.EMAIL, "hello@abofonsa.com", true, 1);
        socialLink(SocialPlatform.X, "Abofonsa on X", false, 2);

        // Clear the persistence context before the endpoint queries it. The highlights were saved
        // against a managed CareServiceTeaser whose own `highlights` collection was never touched,
        // so the fetch join would otherwise be handed that cached instance — with an empty set —
        // and the ordering assertion would fail on data that is perfectly correct in the database.
        entityManager.flush();
        entityManager.clear();

        publicContentService.evictContent();
    }

    @Test
    @Transactional
    void anonymousCanReadTheLaunchContent() throws Exception {
        restMockMvc
            .perform(get(CONTENT_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.launch.organisationName").value("Abofonsa BridgeCare"))
            .andExpect(jsonPath("$.launch.launchAt").value("2027-02-01T00:00:00Z"))
            .andExpect(jsonPath("$.launch.fundUrl").value("https://fund.abofonsa.com"))
            // The consultancy that owns the product, surfaced top and bottom of the page.
            .andExpect(jsonPath("$.launch.parentCompanyName").value("Jojo Addison Consultancy"))
            .andExpect(jsonPath("$.launch.parentCompanyUrl").value("https://jojoaddison.net"))
            .andExpect(jsonPath("$.plans[0].code").value("PAWPAW"))
            .andExpect(jsonPath("$.pledgeTiers[0].handoffUrl").value("https://fund.abofonsa.com/pledge?tier=GOLD"))
            .andExpect(jsonPath("$.milestones[0].title").value("Closed pilot"));
    }

    /** Highlights are a Set on the entity, so Hibernate returns them unordered; the service sorts. */
    @Test
    @Transactional
    void highlightsComeBackInDisplayOrder() throws Exception {
        restMockMvc
            .perform(get(CONTENT_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.services[0].slug").value("skilled-nursing-visits"))
            .andExpect(jsonPath("$.services[0].highlights[0]").value("Vital signs, wound care and dressings"))
            .andExpect(jsonPath("$.services[0].highlights[1]").value("Chronic condition management"));
    }

    @Test
    @Transactional
    void contentNeverExposesUnpublishedOrInactiveRows() throws Exception {
        restMockMvc
            .perform(get(CONTENT_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.services.length()").value(1))
            .andExpect(jsonPath("$.services[*].slug", org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("not-ready-yet"))))
            .andExpect(jsonPath("$.socialLinks.length()").value(1))
            .andExpect(jsonPath("$.socialLinks[0].platform").value("EMAIL"));
    }

    @Test
    @Transactional
    void capturesASignupAndIssuesAnOptInToken() throws Exception {
        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("New@Clinic.org")))
            )
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.status").value("PENDING_CONFIRMATION"))
            .andExpect(jsonPath("$.alreadyRegistered").value(false))
            // The opt-in token is emailed, never returned — echoing it would defeat double opt-in.
            .andExpect(jsonPath("$.reference").doesNotExist());

        WaitlistSignup saved = waitlistSignupRepository.findByEmailNormalized("new@clinic.org").orElseThrow();
        assertThat(saved.getEmail()).isEqualTo("New@Clinic.org"); // original casing preserved
        assertThat(saved.getStatus()).isEqualTo(SignupStatus.PENDING);
        assertThat(saved.getConfirmationToken()).isNotBlank();
        assertThat(saved.getConsentGiven()).isTrue();
    }

    @Test
    @Transactional
    void treatsADifferentlyCasedAddressAsTheSamePerson() throws Exception {
        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("Kofi@Clinic.org")))
            )
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.alreadyRegistered").value(false));

        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("KOFI@clinic.ORG")))
            )
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.alreadyRegistered").value(true));

        assertThat(
            waitlistSignupRepository
                .findAll()
                .stream()
                .filter(s -> "kofi@clinic.org".equals(s.getEmailNormalized()))
        ).hasSize(1);
    }

    @Test
    @Transactional
    void confirmingTheTokenMovesTheSignupToConfirmed() throws Exception {
        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("opt@clinic.org")))
            )
            .andExpect(status().isAccepted());

        String token = waitlistSignupRepository.findByEmailNormalized("opt@clinic.org").orElseThrow().getConfirmationToken();

        restMockMvc
            .perform(postToken(WAITLIST_URL + "/confirm", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"));

        WaitlistSignup confirmed = waitlistSignupRepository.findByEmailNormalized("opt@clinic.org").orElseThrow();
        assertThat(confirmed.getStatus()).isEqualTo(SignupStatus.CONFIRMED);
        // Consumed. Replaying a link out of an old mailbox must not do anything a second time.
        assertThat(confirmed.getConfirmationToken()).isNull();
    }

    /**
     * A confirmation token stops working once it has been used, and once it is past its expiry.
     * Neither was true when the token was permanent and doubled as the unsubscribe credential.
     */
    @Test
    @Transactional
    void aUsedOrExpiredConfirmationTokenIsRefused() throws Exception {
        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("expiry@clinic.org")))
            )
            .andExpect(status().isAccepted());

        WaitlistSignup signup = waitlistSignupRepository.findByEmailNormalized("expiry@clinic.org").orElseThrow();
        String token = signup.getConfirmationToken();

        signup.setConfirmationExpiresAt(Instant.now().minus(1, java.time.temporal.ChronoUnit.HOURS));
        waitlistSignupRepository.saveAndFlush(signup);

        restMockMvc
            .perform(postToken(WAITLIST_URL + "/confirm", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("invalid"));
        assertThat(waitlistSignupRepository.findByEmailNormalized("expiry@clinic.org").orElseThrow().getStatus()).isEqualTo(
            SignupStatus.PENDING
        );
    }

    /**
     * Unsubscribing has its own credential. Presenting the confirmation token here must do nothing —
     * one leaked link used to grant both, in perpetuity.
     */
    @Test
    @Transactional
    void unsubscribingRequiresItsOwnTokenAndNotTheConfirmationOne() throws Exception {
        restMockMvc
            .perform(
                post(WAITLIST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(submission("out@clinic.org")))
            )
            .andExpect(status().isAccepted());

        WaitlistSignup signup = waitlistSignupRepository.findByEmailNormalized("out@clinic.org").orElseThrow();
        assertThat(signup.getUnsubscribeToken()).isNotBlank().isNotEqualTo(signup.getConfirmationToken());

        restMockMvc
            .perform(postToken(WAITLIST_URL + "/unsubscribe", signup.getConfirmationToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("invalid"));
        assertThat(waitlistSignupRepository.findByEmailNormalized("out@clinic.org").orElseThrow().getStatus()).isEqualTo(
            SignupStatus.PENDING
        );

        restMockMvc
            .perform(postToken(WAITLIST_URL + "/unsubscribe", signup.getUnsubscribeToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"));
        assertThat(waitlistSignupRepository.findByEmailNormalized("out@clinic.org").orElseThrow().getStatus()).isEqualTo(
            SignupStatus.UNSUBSCRIBED
        );
    }

    @Test
    @Transactional
    void anUnknownTokenIsRejectedRatherThanConfirmingSomethingElse() throws Exception {
        restMockMvc
            .perform(postToken(WAITLIST_URL + "/confirm", "not-a-real-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("invalid"));
    }

    /**
     * These are POST, not GET. They were side-effecting GETs answered straight from the URL in the
     * email, and mail clients and security gateways prefetch links — so a scanner could confirm a
     * subscription the recipient never agreed to, and the consent record would record its click.
     */
    @Test
    @Transactional
    void theOptInEndpointsRefuseGet() throws Exception {
        restMockMvc.perform(get(WAITLIST_URL + "/confirm").param("token", "anything")).andExpect(status().isMethodNotAllowed());
        restMockMvc.perform(get(WAITLIST_URL + "/unsubscribe").param("token", "anything")).andExpect(status().isMethodNotAllowed());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postToken(String url, String token)
        throws Exception {
        return post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsBytes(Map.of("token", token)));
    }

    @Test
    @Transactional
    void rejectsASubmissionThatFilledTheHoneypot() throws Exception {
        String body = om.writeValueAsString(Map.of("email", "bot@spam.test", "consent", true, "dwellMs", 5000, "company", "AcmeBot"));

        restMockMvc.perform(post(WAITLIST_URL).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());

        assertThat(waitlistSignupRepository.findByEmailNormalized("bot@spam.test")).isEmpty();
    }

    @Test
    @Transactional
    void rejectsASubmissionThatArrivedTooFastToHaveBeenTyped() throws Exception {
        String body = om.writeValueAsString(Map.of("email", "fast@spam.test", "consent", true, "dwellMs", 40));

        restMockMvc.perform(post(WAITLIST_URL).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());

        assertThat(waitlistSignupRepository.findByEmailNormalized("fast@spam.test")).isEmpty();
    }

    @Test
    @Transactional
    void requiresConsent() throws Exception {
        String body = om.writeValueAsString(Map.of("email", "noconsent@clinic.org", "consent", false, "dwellMs", 5000));

        restMockMvc.perform(post(WAITLIST_URL).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());

        assertThat(waitlistSignupRepository.findByEmailNormalized("noconsent@clinic.org")).isEmpty();
    }

    @Test
    @Transactional
    void acceptsABrowserReportableBeaconEvent() throws Exception {
        long before = captureEventRepository.count();

        restMockMvc
            .perform(
                post(EVENTS_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(Map.of("eventType", "PLEDGE_CTA_CLICK", "targetKey", "GOLD")))
            )
            .andExpect(status().isNoContent());

        assertThat(captureEventRepository.count()).isEqualTo(before + 1);
    }

    /**
     * The important half of the beacon's contract: the signup count is the number the dashboard
     * exists to report, so the browser must not be able to increment it without signing up.
     */
    @Test
    @Transactional
    void refusesAServerAttestedEventTypeFromTheBrowser() throws Exception {
        long before = captureEventRepository.count();

        for (CaptureEventType forged : List.of(
            CaptureEventType.WAITLIST_SUBMIT,
            CaptureEventType.WAITLIST_CONFIRM,
            CaptureEventType.WAITLIST_DUPLICATE
        )) {
            restMockMvc
                .perform(
                    post(EVENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("eventType", forged.name())))
                )
                .andExpect(status().isBadRequest());
        }

        assertThat(captureEventRepository.count()).isEqualTo(before);
    }

    private void highlight(CareServiceTeaser service, String label, int order) {
        ServiceHighlight highlight = new ServiceHighlight();
        highlight.setLabel(label);
        highlight.setDisplayOrder(order);
        highlight.setService(service);
        serviceHighlightRepository.saveAndFlush(highlight);
    }

    private void socialLink(SocialPlatform platform, String label, boolean active, int order) {
        SocialLink link = new SocialLink();
        link.setPlatform(platform);
        link.setLabel(label);
        link.setUrl("https://example.test/" + platform.name().toLowerCase());
        link.setDisplayOrder(order);
        link.setActive(active);
        socialLinkRepository.saveAndFlush(link);
    }

    private Map<String, Object> submission(String email) {
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("email", email);
        body.put("consent", true);
        body.put("dwellMs", 5000);
        body.put("locale", "en");
        body.put("sourcePage", "/");
        return body;
    }
}
