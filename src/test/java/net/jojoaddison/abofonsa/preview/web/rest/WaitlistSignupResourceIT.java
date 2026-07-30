package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.WaitlistSignupAsserts.*;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.AudienceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSignupDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.WaitlistSignupMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WaitlistSignupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WaitlistSignupResourceIT {

    private static final String DEFAULT_EMAIL = "o=>CP@w.mS";
    private static final String UPDATED_EMAIL = "Fm@K# y<.r";

    private static final String DEFAULT_EMAIL_NORMALIZED = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_NORMALIZED = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORGANISATION = "AAAAAAAAAA";
    private static final String UPDATED_ORGANISATION = "BBBBBBBBBB";

    private static final AudienceType DEFAULT_AUDIENCE = AudienceType.CLINIC;
    private static final AudienceType UPDATED_AUDIENCE = AudienceType.PHARMACY;

    private static final PlanCode DEFAULT_PLAN_OF_INTEREST = PlanCode.PEAR;
    private static final PlanCode UPDATED_PLAN_OF_INTEREST = PlanCode.PAWPAW;

    private static final SignupStatus DEFAULT_STATUS = SignupStatus.PENDING;
    private static final SignupStatus UPDATED_STATUS = SignupStatus.CONFIRMED;

    private static final String DEFAULT_LOCALE = "AAAAAAAAAA";
    private static final String UPDATED_LOCALE = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_PAGE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_PAGE = "BBBBBBBBBB";

    private static final String DEFAULT_UTM_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_UTM_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_UTM_MEDIUM = "AAAAAAAAAA";
    private static final String UPDATED_UTM_MEDIUM = "BBBBBBBBBB";

    private static final String DEFAULT_UTM_CAMPAIGN = "AAAAAAAAAA";
    private static final String UPDATED_UTM_CAMPAIGN = "BBBBBBBBBB";

    private static final String DEFAULT_REFERRER = "AAAAAAAAAA";
    private static final String UPDATED_REFERRER = "BBBBBBBBBB";

    private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.DESKTOP;
    private static final DeviceType UPDATED_DEVICE_TYPE = DeviceType.MOBILE;

    private static final Boolean DEFAULT_CONSENT_GIVEN = false;
    private static final Boolean UPDATED_CONSENT_GIVEN = true;

    private static final String DEFAULT_CONFIRMATION_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_CONFIRMATION_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UNSUBSCRIBED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UNSUBSCRIBED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CAPTURED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CAPTURED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_HASH = "AAAAAAAAAA";
    private static final String UPDATED_IP_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/waitlist-signups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WaitlistSignupRepository waitlistSignupRepository;

    @Autowired
    private WaitlistSignupMapper waitlistSignupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWaitlistSignupMockMvc;

    private WaitlistSignup waitlistSignup;

    private WaitlistSignup insertedWaitlistSignup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitlistSignup createEntity() {
        return new WaitlistSignup()
            .email(DEFAULT_EMAIL)
            .emailNormalized(DEFAULT_EMAIL_NORMALIZED)
            .fullName(DEFAULT_FULL_NAME)
            .organisation(DEFAULT_ORGANISATION)
            .audience(DEFAULT_AUDIENCE)
            .planOfInterest(DEFAULT_PLAN_OF_INTEREST)
            .status(DEFAULT_STATUS)
            .locale(DEFAULT_LOCALE)
            .sourcePage(DEFAULT_SOURCE_PAGE)
            .utmSource(DEFAULT_UTM_SOURCE)
            .utmMedium(DEFAULT_UTM_MEDIUM)
            .utmCampaign(DEFAULT_UTM_CAMPAIGN)
            .referrer(DEFAULT_REFERRER)
            .deviceType(DEFAULT_DEVICE_TYPE)
            .consentGiven(DEFAULT_CONSENT_GIVEN)
            .confirmationToken(DEFAULT_CONFIRMATION_TOKEN)
            .confirmedAt(DEFAULT_CONFIRMED_AT)
            .unsubscribedAt(DEFAULT_UNSUBSCRIBED_AT)
            .capturedAt(DEFAULT_CAPTURED_AT)
            .ipHash(DEFAULT_IP_HASH)
            .userAgent(DEFAULT_USER_AGENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WaitlistSignup createUpdatedEntity() {
        return new WaitlistSignup()
            .email(UPDATED_EMAIL)
            .emailNormalized(UPDATED_EMAIL_NORMALIZED)
            .fullName(UPDATED_FULL_NAME)
            .organisation(UPDATED_ORGANISATION)
            .audience(UPDATED_AUDIENCE)
            .planOfInterest(UPDATED_PLAN_OF_INTEREST)
            .status(UPDATED_STATUS)
            .locale(UPDATED_LOCALE)
            .sourcePage(UPDATED_SOURCE_PAGE)
            .utmSource(UPDATED_UTM_SOURCE)
            .utmMedium(UPDATED_UTM_MEDIUM)
            .utmCampaign(UPDATED_UTM_CAMPAIGN)
            .referrer(UPDATED_REFERRER)
            .deviceType(UPDATED_DEVICE_TYPE)
            .consentGiven(UPDATED_CONSENT_GIVEN)
            .confirmationToken(UPDATED_CONFIRMATION_TOKEN)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .unsubscribedAt(UPDATED_UNSUBSCRIBED_AT)
            .capturedAt(UPDATED_CAPTURED_AT)
            .ipHash(UPDATED_IP_HASH)
            .userAgent(UPDATED_USER_AGENT);
    }

    @BeforeEach
    void initTest() {
        waitlistSignup = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWaitlistSignup != null) {
            waitlistSignupRepository.delete(insertedWaitlistSignup);
            insertedWaitlistSignup = null;
        }
    }

    @Test
    @Transactional
    void createWaitlistSignup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);
        var returnedWaitlistSignupDTO = om.readValue(
            restWaitlistSignupMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WaitlistSignupDTO.class
        );

        // Validate the WaitlistSignup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWaitlistSignup = waitlistSignupMapper.toEntity(returnedWaitlistSignupDTO);
        assertWaitlistSignupUpdatableFieldsEquals(returnedWaitlistSignup, getPersistedWaitlistSignup(returnedWaitlistSignup));

        insertedWaitlistSignup = returnedWaitlistSignup;
    }

    @Test
    @Transactional
    void createWaitlistSignupWithExistingId() throws Exception {
        // Create the WaitlistSignup with an existing ID
        waitlistSignup.setId(1L);
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitlistSignup.setEmail(null);

        // Create the WaitlistSignup, which fails.
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailNormalizedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitlistSignup.setEmailNormalized(null);

        // Create the WaitlistSignup, which fails.
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitlistSignup.setStatus(null);

        // Create the WaitlistSignup, which fails.
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConsentGivenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitlistSignup.setConsentGiven(null);

        // Create the WaitlistSignup, which fails.
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapturedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waitlistSignup.setCapturedAt(null);

        // Create the WaitlistSignup, which fails.
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        restWaitlistSignupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWaitlistSignups() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waitlistSignup.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].emailNormalized").value(hasItem(DEFAULT_EMAIL_NORMALIZED)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].organisation").value(hasItem(DEFAULT_ORGANISATION)))
            .andExpect(jsonPath("$.[*].audience").value(hasItem(DEFAULT_AUDIENCE.toString())))
            .andExpect(jsonPath("$.[*].planOfInterest").value(hasItem(DEFAULT_PLAN_OF_INTEREST.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].locale").value(hasItem(DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].sourcePage").value(hasItem(DEFAULT_SOURCE_PAGE)))
            .andExpect(jsonPath("$.[*].utmSource").value(hasItem(DEFAULT_UTM_SOURCE)))
            .andExpect(jsonPath("$.[*].utmMedium").value(hasItem(DEFAULT_UTM_MEDIUM)))
            .andExpect(jsonPath("$.[*].utmCampaign").value(hasItem(DEFAULT_UTM_CAMPAIGN)))
            .andExpect(jsonPath("$.[*].referrer").value(hasItem(DEFAULT_REFERRER)))
            .andExpect(jsonPath("$.[*].deviceType").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].consentGiven").value(hasItem(DEFAULT_CONSENT_GIVEN)))
            .andExpect(jsonPath("$.[*].confirmationToken").value(hasItem(DEFAULT_CONFIRMATION_TOKEN)))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].unsubscribedAt").value(hasItem(DEFAULT_UNSUBSCRIBED_AT.toString())))
            .andExpect(jsonPath("$.[*].capturedAt").value(hasItem(DEFAULT_CAPTURED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipHash").value(hasItem(DEFAULT_IP_HASH)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));
    }

    @Test
    @Transactional
    void getWaitlistSignup() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get the waitlistSignup
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL_ID, waitlistSignup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(waitlistSignup.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.emailNormalized").value(DEFAULT_EMAIL_NORMALIZED))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.organisation").value(DEFAULT_ORGANISATION))
            .andExpect(jsonPath("$.audience").value(DEFAULT_AUDIENCE.toString()))
            .andExpect(jsonPath("$.planOfInterest").value(DEFAULT_PLAN_OF_INTEREST.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.locale").value(DEFAULT_LOCALE))
            .andExpect(jsonPath("$.sourcePage").value(DEFAULT_SOURCE_PAGE))
            .andExpect(jsonPath("$.utmSource").value(DEFAULT_UTM_SOURCE))
            .andExpect(jsonPath("$.utmMedium").value(DEFAULT_UTM_MEDIUM))
            .andExpect(jsonPath("$.utmCampaign").value(DEFAULT_UTM_CAMPAIGN))
            .andExpect(jsonPath("$.referrer").value(DEFAULT_REFERRER))
            .andExpect(jsonPath("$.deviceType").value(DEFAULT_DEVICE_TYPE.toString()))
            .andExpect(jsonPath("$.consentGiven").value(DEFAULT_CONSENT_GIVEN))
            .andExpect(jsonPath("$.confirmationToken").value(DEFAULT_CONFIRMATION_TOKEN))
            .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()))
            .andExpect(jsonPath("$.unsubscribedAt").value(DEFAULT_UNSUBSCRIBED_AT.toString()))
            .andExpect(jsonPath("$.capturedAt").value(DEFAULT_CAPTURED_AT.toString()))
            .andExpect(jsonPath("$.ipHash").value(DEFAULT_IP_HASH))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT));
    }

    @Test
    @Transactional
    void getWaitlistSignupsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        Long id = waitlistSignup.getId();

        defaultWaitlistSignupFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWaitlistSignupFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWaitlistSignupFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where email equals to
        defaultWaitlistSignupFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where email in
        defaultWaitlistSignupFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where email is not null
        defaultWaitlistSignupFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where email contains
        defaultWaitlistSignupFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where email does not contain
        defaultWaitlistSignupFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNormalizedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where emailNormalized equals to
        defaultWaitlistSignupFiltering(
            "emailNormalized.equals=" + DEFAULT_EMAIL_NORMALIZED,
            "emailNormalized.equals=" + UPDATED_EMAIL_NORMALIZED
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNormalizedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where emailNormalized in
        defaultWaitlistSignupFiltering(
            "emailNormalized.in=" + DEFAULT_EMAIL_NORMALIZED + "," + UPDATED_EMAIL_NORMALIZED,
            "emailNormalized.in=" + UPDATED_EMAIL_NORMALIZED
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNormalizedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where emailNormalized is not null
        defaultWaitlistSignupFiltering("emailNormalized.specified=true", "emailNormalized.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNormalizedContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where emailNormalized contains
        defaultWaitlistSignupFiltering(
            "emailNormalized.contains=" + DEFAULT_EMAIL_NORMALIZED,
            "emailNormalized.contains=" + UPDATED_EMAIL_NORMALIZED
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByEmailNormalizedNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where emailNormalized does not contain
        defaultWaitlistSignupFiltering(
            "emailNormalized.doesNotContain=" + UPDATED_EMAIL_NORMALIZED,
            "emailNormalized.doesNotContain=" + DEFAULT_EMAIL_NORMALIZED
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where fullName equals to
        defaultWaitlistSignupFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where fullName in
        defaultWaitlistSignupFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where fullName is not null
        defaultWaitlistSignupFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where fullName contains
        defaultWaitlistSignupFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where fullName does not contain
        defaultWaitlistSignupFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByOrganisationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where organisation equals to
        defaultWaitlistSignupFiltering("organisation.equals=" + DEFAULT_ORGANISATION, "organisation.equals=" + UPDATED_ORGANISATION);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByOrganisationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where organisation in
        defaultWaitlistSignupFiltering(
            "organisation.in=" + DEFAULT_ORGANISATION + "," + UPDATED_ORGANISATION,
            "organisation.in=" + UPDATED_ORGANISATION
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByOrganisationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where organisation is not null
        defaultWaitlistSignupFiltering("organisation.specified=true", "organisation.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByOrganisationContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where organisation contains
        defaultWaitlistSignupFiltering("organisation.contains=" + DEFAULT_ORGANISATION, "organisation.contains=" + UPDATED_ORGANISATION);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByOrganisationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where organisation does not contain
        defaultWaitlistSignupFiltering(
            "organisation.doesNotContain=" + UPDATED_ORGANISATION,
            "organisation.doesNotContain=" + DEFAULT_ORGANISATION
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByAudienceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where audience equals to
        defaultWaitlistSignupFiltering("audience.equals=" + DEFAULT_AUDIENCE, "audience.equals=" + UPDATED_AUDIENCE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByAudienceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where audience in
        defaultWaitlistSignupFiltering("audience.in=" + DEFAULT_AUDIENCE + "," + UPDATED_AUDIENCE, "audience.in=" + UPDATED_AUDIENCE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByAudienceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where audience is not null
        defaultWaitlistSignupFiltering("audience.specified=true", "audience.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByPlanOfInterestIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where planOfInterest equals to
        defaultWaitlistSignupFiltering(
            "planOfInterest.equals=" + DEFAULT_PLAN_OF_INTEREST,
            "planOfInterest.equals=" + UPDATED_PLAN_OF_INTEREST
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByPlanOfInterestIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where planOfInterest in
        defaultWaitlistSignupFiltering(
            "planOfInterest.in=" + DEFAULT_PLAN_OF_INTEREST + "," + UPDATED_PLAN_OF_INTEREST,
            "planOfInterest.in=" + UPDATED_PLAN_OF_INTEREST
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByPlanOfInterestIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where planOfInterest is not null
        defaultWaitlistSignupFiltering("planOfInterest.specified=true", "planOfInterest.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where status equals to
        defaultWaitlistSignupFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where status in
        defaultWaitlistSignupFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where status is not null
        defaultWaitlistSignupFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByLocaleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where locale equals to
        defaultWaitlistSignupFiltering("locale.equals=" + DEFAULT_LOCALE, "locale.equals=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByLocaleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where locale in
        defaultWaitlistSignupFiltering("locale.in=" + DEFAULT_LOCALE + "," + UPDATED_LOCALE, "locale.in=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByLocaleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where locale is not null
        defaultWaitlistSignupFiltering("locale.specified=true", "locale.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByLocaleContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where locale contains
        defaultWaitlistSignupFiltering("locale.contains=" + DEFAULT_LOCALE, "locale.contains=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByLocaleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where locale does not contain
        defaultWaitlistSignupFiltering("locale.doesNotContain=" + UPDATED_LOCALE, "locale.doesNotContain=" + DEFAULT_LOCALE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsBySourcePageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where sourcePage equals to
        defaultWaitlistSignupFiltering("sourcePage.equals=" + DEFAULT_SOURCE_PAGE, "sourcePage.equals=" + UPDATED_SOURCE_PAGE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsBySourcePageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where sourcePage in
        defaultWaitlistSignupFiltering(
            "sourcePage.in=" + DEFAULT_SOURCE_PAGE + "," + UPDATED_SOURCE_PAGE,
            "sourcePage.in=" + UPDATED_SOURCE_PAGE
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsBySourcePageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where sourcePage is not null
        defaultWaitlistSignupFiltering("sourcePage.specified=true", "sourcePage.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsBySourcePageContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where sourcePage contains
        defaultWaitlistSignupFiltering("sourcePage.contains=" + DEFAULT_SOURCE_PAGE, "sourcePage.contains=" + UPDATED_SOURCE_PAGE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsBySourcePageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where sourcePage does not contain
        defaultWaitlistSignupFiltering(
            "sourcePage.doesNotContain=" + UPDATED_SOURCE_PAGE,
            "sourcePage.doesNotContain=" + DEFAULT_SOURCE_PAGE
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmSourceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmSource equals to
        defaultWaitlistSignupFiltering("utmSource.equals=" + DEFAULT_UTM_SOURCE, "utmSource.equals=" + UPDATED_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmSourceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmSource in
        defaultWaitlistSignupFiltering(
            "utmSource.in=" + DEFAULT_UTM_SOURCE + "," + UPDATED_UTM_SOURCE,
            "utmSource.in=" + UPDATED_UTM_SOURCE
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmSourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmSource is not null
        defaultWaitlistSignupFiltering("utmSource.specified=true", "utmSource.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmSourceContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmSource contains
        defaultWaitlistSignupFiltering("utmSource.contains=" + DEFAULT_UTM_SOURCE, "utmSource.contains=" + UPDATED_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmSourceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmSource does not contain
        defaultWaitlistSignupFiltering("utmSource.doesNotContain=" + UPDATED_UTM_SOURCE, "utmSource.doesNotContain=" + DEFAULT_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmMediumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmMedium equals to
        defaultWaitlistSignupFiltering("utmMedium.equals=" + DEFAULT_UTM_MEDIUM, "utmMedium.equals=" + UPDATED_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmMediumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmMedium in
        defaultWaitlistSignupFiltering(
            "utmMedium.in=" + DEFAULT_UTM_MEDIUM + "," + UPDATED_UTM_MEDIUM,
            "utmMedium.in=" + UPDATED_UTM_MEDIUM
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmMediumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmMedium is not null
        defaultWaitlistSignupFiltering("utmMedium.specified=true", "utmMedium.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmMediumContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmMedium contains
        defaultWaitlistSignupFiltering("utmMedium.contains=" + DEFAULT_UTM_MEDIUM, "utmMedium.contains=" + UPDATED_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmMediumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmMedium does not contain
        defaultWaitlistSignupFiltering("utmMedium.doesNotContain=" + UPDATED_UTM_MEDIUM, "utmMedium.doesNotContain=" + DEFAULT_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmCampaignIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmCampaign equals to
        defaultWaitlistSignupFiltering("utmCampaign.equals=" + DEFAULT_UTM_CAMPAIGN, "utmCampaign.equals=" + UPDATED_UTM_CAMPAIGN);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmCampaignIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmCampaign in
        defaultWaitlistSignupFiltering(
            "utmCampaign.in=" + DEFAULT_UTM_CAMPAIGN + "," + UPDATED_UTM_CAMPAIGN,
            "utmCampaign.in=" + UPDATED_UTM_CAMPAIGN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmCampaignIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmCampaign is not null
        defaultWaitlistSignupFiltering("utmCampaign.specified=true", "utmCampaign.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmCampaignContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmCampaign contains
        defaultWaitlistSignupFiltering("utmCampaign.contains=" + DEFAULT_UTM_CAMPAIGN, "utmCampaign.contains=" + UPDATED_UTM_CAMPAIGN);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUtmCampaignNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where utmCampaign does not contain
        defaultWaitlistSignupFiltering(
            "utmCampaign.doesNotContain=" + UPDATED_UTM_CAMPAIGN,
            "utmCampaign.doesNotContain=" + DEFAULT_UTM_CAMPAIGN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByReferrerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where referrer equals to
        defaultWaitlistSignupFiltering("referrer.equals=" + DEFAULT_REFERRER, "referrer.equals=" + UPDATED_REFERRER);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByReferrerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where referrer in
        defaultWaitlistSignupFiltering("referrer.in=" + DEFAULT_REFERRER + "," + UPDATED_REFERRER, "referrer.in=" + UPDATED_REFERRER);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByReferrerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where referrer is not null
        defaultWaitlistSignupFiltering("referrer.specified=true", "referrer.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByReferrerContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where referrer contains
        defaultWaitlistSignupFiltering("referrer.contains=" + DEFAULT_REFERRER, "referrer.contains=" + UPDATED_REFERRER);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByReferrerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where referrer does not contain
        defaultWaitlistSignupFiltering("referrer.doesNotContain=" + UPDATED_REFERRER, "referrer.doesNotContain=" + DEFAULT_REFERRER);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByDeviceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where deviceType equals to
        defaultWaitlistSignupFiltering("deviceType.equals=" + DEFAULT_DEVICE_TYPE, "deviceType.equals=" + UPDATED_DEVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByDeviceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where deviceType in
        defaultWaitlistSignupFiltering(
            "deviceType.in=" + DEFAULT_DEVICE_TYPE + "," + UPDATED_DEVICE_TYPE,
            "deviceType.in=" + UPDATED_DEVICE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByDeviceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where deviceType is not null
        defaultWaitlistSignupFiltering("deviceType.specified=true", "deviceType.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConsentGivenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where consentGiven equals to
        defaultWaitlistSignupFiltering("consentGiven.equals=" + DEFAULT_CONSENT_GIVEN, "consentGiven.equals=" + UPDATED_CONSENT_GIVEN);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConsentGivenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where consentGiven in
        defaultWaitlistSignupFiltering(
            "consentGiven.in=" + DEFAULT_CONSENT_GIVEN + "," + UPDATED_CONSENT_GIVEN,
            "consentGiven.in=" + UPDATED_CONSENT_GIVEN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConsentGivenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where consentGiven is not null
        defaultWaitlistSignupFiltering("consentGiven.specified=true", "consentGiven.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmationTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmationToken equals to
        defaultWaitlistSignupFiltering(
            "confirmationToken.equals=" + DEFAULT_CONFIRMATION_TOKEN,
            "confirmationToken.equals=" + UPDATED_CONFIRMATION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmationTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmationToken in
        defaultWaitlistSignupFiltering(
            "confirmationToken.in=" + DEFAULT_CONFIRMATION_TOKEN + "," + UPDATED_CONFIRMATION_TOKEN,
            "confirmationToken.in=" + UPDATED_CONFIRMATION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmationTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmationToken is not null
        defaultWaitlistSignupFiltering("confirmationToken.specified=true", "confirmationToken.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmationTokenContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmationToken contains
        defaultWaitlistSignupFiltering(
            "confirmationToken.contains=" + DEFAULT_CONFIRMATION_TOKEN,
            "confirmationToken.contains=" + UPDATED_CONFIRMATION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmationTokenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmationToken does not contain
        defaultWaitlistSignupFiltering(
            "confirmationToken.doesNotContain=" + UPDATED_CONFIRMATION_TOKEN,
            "confirmationToken.doesNotContain=" + DEFAULT_CONFIRMATION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmedAt equals to
        defaultWaitlistSignupFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT, "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmedAt in
        defaultWaitlistSignupFiltering(
            "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
            "confirmedAt.in=" + UPDATED_CONFIRMED_AT
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where confirmedAt is not null
        defaultWaitlistSignupFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUnsubscribedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where unsubscribedAt equals to
        defaultWaitlistSignupFiltering(
            "unsubscribedAt.equals=" + DEFAULT_UNSUBSCRIBED_AT,
            "unsubscribedAt.equals=" + UPDATED_UNSUBSCRIBED_AT
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUnsubscribedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where unsubscribedAt in
        defaultWaitlistSignupFiltering(
            "unsubscribedAt.in=" + DEFAULT_UNSUBSCRIBED_AT + "," + UPDATED_UNSUBSCRIBED_AT,
            "unsubscribedAt.in=" + UPDATED_UNSUBSCRIBED_AT
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUnsubscribedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where unsubscribedAt is not null
        defaultWaitlistSignupFiltering("unsubscribedAt.specified=true", "unsubscribedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByCapturedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where capturedAt equals to
        defaultWaitlistSignupFiltering("capturedAt.equals=" + DEFAULT_CAPTURED_AT, "capturedAt.equals=" + UPDATED_CAPTURED_AT);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByCapturedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where capturedAt in
        defaultWaitlistSignupFiltering(
            "capturedAt.in=" + DEFAULT_CAPTURED_AT + "," + UPDATED_CAPTURED_AT,
            "capturedAt.in=" + UPDATED_CAPTURED_AT
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByCapturedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where capturedAt is not null
        defaultWaitlistSignupFiltering("capturedAt.specified=true", "capturedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByIpHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where ipHash equals to
        defaultWaitlistSignupFiltering("ipHash.equals=" + DEFAULT_IP_HASH, "ipHash.equals=" + UPDATED_IP_HASH);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByIpHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where ipHash in
        defaultWaitlistSignupFiltering("ipHash.in=" + DEFAULT_IP_HASH + "," + UPDATED_IP_HASH, "ipHash.in=" + UPDATED_IP_HASH);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByIpHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where ipHash is not null
        defaultWaitlistSignupFiltering("ipHash.specified=true", "ipHash.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByIpHashContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where ipHash contains
        defaultWaitlistSignupFiltering("ipHash.contains=" + DEFAULT_IP_HASH, "ipHash.contains=" + UPDATED_IP_HASH);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByIpHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where ipHash does not contain
        defaultWaitlistSignupFiltering("ipHash.doesNotContain=" + UPDATED_IP_HASH, "ipHash.doesNotContain=" + DEFAULT_IP_HASH);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUserAgentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where userAgent equals to
        defaultWaitlistSignupFiltering("userAgent.equals=" + DEFAULT_USER_AGENT, "userAgent.equals=" + UPDATED_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUserAgentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where userAgent in
        defaultWaitlistSignupFiltering(
            "userAgent.in=" + DEFAULT_USER_AGENT + "," + UPDATED_USER_AGENT,
            "userAgent.in=" + UPDATED_USER_AGENT
        );
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUserAgentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where userAgent is not null
        defaultWaitlistSignupFiltering("userAgent.specified=true", "userAgent.specified=false");
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUserAgentContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where userAgent contains
        defaultWaitlistSignupFiltering("userAgent.contains=" + DEFAULT_USER_AGENT, "userAgent.contains=" + UPDATED_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllWaitlistSignupsByUserAgentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        // Get all the waitlistSignupList where userAgent does not contain
        defaultWaitlistSignupFiltering("userAgent.doesNotContain=" + UPDATED_USER_AGENT, "userAgent.doesNotContain=" + DEFAULT_USER_AGENT);
    }

    private void defaultWaitlistSignupFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWaitlistSignupShouldBeFound(shouldBeFound);
        defaultWaitlistSignupShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWaitlistSignupShouldBeFound(String filter) throws Exception {
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waitlistSignup.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].emailNormalized").value(hasItem(DEFAULT_EMAIL_NORMALIZED)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].organisation").value(hasItem(DEFAULT_ORGANISATION)))
            .andExpect(jsonPath("$.[*].audience").value(hasItem(DEFAULT_AUDIENCE.toString())))
            .andExpect(jsonPath("$.[*].planOfInterest").value(hasItem(DEFAULT_PLAN_OF_INTEREST.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].locale").value(hasItem(DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].sourcePage").value(hasItem(DEFAULT_SOURCE_PAGE)))
            .andExpect(jsonPath("$.[*].utmSource").value(hasItem(DEFAULT_UTM_SOURCE)))
            .andExpect(jsonPath("$.[*].utmMedium").value(hasItem(DEFAULT_UTM_MEDIUM)))
            .andExpect(jsonPath("$.[*].utmCampaign").value(hasItem(DEFAULT_UTM_CAMPAIGN)))
            .andExpect(jsonPath("$.[*].referrer").value(hasItem(DEFAULT_REFERRER)))
            .andExpect(jsonPath("$.[*].deviceType").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].consentGiven").value(hasItem(DEFAULT_CONSENT_GIVEN)))
            .andExpect(jsonPath("$.[*].confirmationToken").value(hasItem(DEFAULT_CONFIRMATION_TOKEN)))
            .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
            .andExpect(jsonPath("$.[*].unsubscribedAt").value(hasItem(DEFAULT_UNSUBSCRIBED_AT.toString())))
            .andExpect(jsonPath("$.[*].capturedAt").value(hasItem(DEFAULT_CAPTURED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipHash").value(hasItem(DEFAULT_IP_HASH)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));

        // Check, that the count call also returns 1
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWaitlistSignupShouldNotBeFound(String filter) throws Exception {
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWaitlistSignupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWaitlistSignup() throws Exception {
        // Get the waitlistSignup
        restWaitlistSignupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWaitlistSignup() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitlistSignup
        WaitlistSignup updatedWaitlistSignup = waitlistSignupRepository.findById(waitlistSignup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWaitlistSignup are not directly saved in db
        em.detach(updatedWaitlistSignup);
        updatedWaitlistSignup
            .email(UPDATED_EMAIL)
            .emailNormalized(UPDATED_EMAIL_NORMALIZED)
            .fullName(UPDATED_FULL_NAME)
            .organisation(UPDATED_ORGANISATION)
            .audience(UPDATED_AUDIENCE)
            .planOfInterest(UPDATED_PLAN_OF_INTEREST)
            .status(UPDATED_STATUS)
            .locale(UPDATED_LOCALE)
            .sourcePage(UPDATED_SOURCE_PAGE)
            .utmSource(UPDATED_UTM_SOURCE)
            .utmMedium(UPDATED_UTM_MEDIUM)
            .utmCampaign(UPDATED_UTM_CAMPAIGN)
            .referrer(UPDATED_REFERRER)
            .deviceType(UPDATED_DEVICE_TYPE)
            .consentGiven(UPDATED_CONSENT_GIVEN)
            .confirmationToken(UPDATED_CONFIRMATION_TOKEN)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .unsubscribedAt(UPDATED_UNSUBSCRIBED_AT)
            .capturedAt(UPDATED_CAPTURED_AT)
            .ipHash(UPDATED_IP_HASH)
            .userAgent(UPDATED_USER_AGENT);
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(updatedWaitlistSignup);

        restWaitlistSignupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, waitlistSignupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitlistSignupDTO))
            )
            .andExpect(status().isOk());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWaitlistSignupToMatchAllProperties(updatedWaitlistSignup);
    }

    @Test
    @Transactional
    void putNonExistingWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, waitlistSignupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitlistSignupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waitlistSignupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWaitlistSignupWithPatch() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitlistSignup using partial update
        WaitlistSignup partialUpdatedWaitlistSignup = new WaitlistSignup();
        partialUpdatedWaitlistSignup.setId(waitlistSignup.getId());

        partialUpdatedWaitlistSignup
            .emailNormalized(UPDATED_EMAIL_NORMALIZED)
            .organisation(UPDATED_ORGANISATION)
            .planOfInterest(UPDATED_PLAN_OF_INTEREST)
            .utmSource(UPDATED_UTM_SOURCE)
            .utmMedium(UPDATED_UTM_MEDIUM)
            .utmCampaign(UPDATED_UTM_CAMPAIGN)
            .referrer(UPDATED_REFERRER)
            .consentGiven(UPDATED_CONSENT_GIVEN)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .unsubscribedAt(UPDATED_UNSUBSCRIBED_AT)
            .ipHash(UPDATED_IP_HASH)
            .userAgent(UPDATED_USER_AGENT);

        restWaitlistSignupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitlistSignup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaitlistSignup))
            )
            .andExpect(status().isOk());

        // Validate the WaitlistSignup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaitlistSignupUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWaitlistSignup, waitlistSignup),
            getPersistedWaitlistSignup(waitlistSignup)
        );
    }

    @Test
    @Transactional
    void fullUpdateWaitlistSignupWithPatch() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waitlistSignup using partial update
        WaitlistSignup partialUpdatedWaitlistSignup = new WaitlistSignup();
        partialUpdatedWaitlistSignup.setId(waitlistSignup.getId());

        partialUpdatedWaitlistSignup
            .email(UPDATED_EMAIL)
            .emailNormalized(UPDATED_EMAIL_NORMALIZED)
            .fullName(UPDATED_FULL_NAME)
            .organisation(UPDATED_ORGANISATION)
            .audience(UPDATED_AUDIENCE)
            .planOfInterest(UPDATED_PLAN_OF_INTEREST)
            .status(UPDATED_STATUS)
            .locale(UPDATED_LOCALE)
            .sourcePage(UPDATED_SOURCE_PAGE)
            .utmSource(UPDATED_UTM_SOURCE)
            .utmMedium(UPDATED_UTM_MEDIUM)
            .utmCampaign(UPDATED_UTM_CAMPAIGN)
            .referrer(UPDATED_REFERRER)
            .deviceType(UPDATED_DEVICE_TYPE)
            .consentGiven(UPDATED_CONSENT_GIVEN)
            .confirmationToken(UPDATED_CONFIRMATION_TOKEN)
            .confirmedAt(UPDATED_CONFIRMED_AT)
            .unsubscribedAt(UPDATED_UNSUBSCRIBED_AT)
            .capturedAt(UPDATED_CAPTURED_AT)
            .ipHash(UPDATED_IP_HASH)
            .userAgent(UPDATED_USER_AGENT);

        restWaitlistSignupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaitlistSignup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaitlistSignup))
            )
            .andExpect(status().isOk());

        // Validate the WaitlistSignup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaitlistSignupUpdatableFieldsEquals(partialUpdatedWaitlistSignup, getPersistedWaitlistSignup(partialUpdatedWaitlistSignup));
    }

    @Test
    @Transactional
    void patchNonExistingWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, waitlistSignupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(waitlistSignupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(waitlistSignupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWaitlistSignup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waitlistSignup.setId(longCount.incrementAndGet());

        // Create the WaitlistSignup
        WaitlistSignupDTO waitlistSignupDTO = waitlistSignupMapper.toDto(waitlistSignup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaitlistSignupMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(waitlistSignupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WaitlistSignup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWaitlistSignup() throws Exception {
        // Initialize the database
        insertedWaitlistSignup = waitlistSignupRepository.saveAndFlush(waitlistSignup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the waitlistSignup
        restWaitlistSignupMockMvc
            .perform(delete(ENTITY_API_URL_ID, waitlistSignup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return waitlistSignupRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected WaitlistSignup getPersistedWaitlistSignup(WaitlistSignup waitlistSignup) {
        return waitlistSignupRepository.findById(waitlistSignup.getId()).orElseThrow();
    }

    protected void assertPersistedWaitlistSignupToMatchAllProperties(WaitlistSignup expectedWaitlistSignup) {
        assertWaitlistSignupAllPropertiesEquals(expectedWaitlistSignup, getPersistedWaitlistSignup(expectedWaitlistSignup));
    }

    protected void assertPersistedWaitlistSignupToMatchUpdatableProperties(WaitlistSignup expectedWaitlistSignup) {
        assertWaitlistSignupAllUpdatablePropertiesEquals(expectedWaitlistSignup, getPersistedWaitlistSignup(expectedWaitlistSignup));
    }
}
