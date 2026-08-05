package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.CaptureEventAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.mapper.CaptureEventMapper;
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
 * Integration tests for the {@link CaptureEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class CaptureEventResourceIT {

    private static final CaptureEventType DEFAULT_EVENT_TYPE = CaptureEventType.PAGE_VIEW;
    private static final CaptureEventType UPDATED_EVENT_TYPE = CaptureEventType.WAITLIST_SUBMIT;

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDate DEFAULT_OCCURRED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_OCCURRED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_OCCURRED_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_SESSION_HASH = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_HASH = "BBBBBBBBBB";

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

    private static final String DEFAULT_REFERRER_HOST = "AAAAAAAAAA";
    private static final String UPDATED_REFERRER_HOST = "BBBBBBBBBB";

    private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.DESKTOP;
    private static final DeviceType UPDATED_DEVICE_TYPE = DeviceType.MOBILE;

    private static final String DEFAULT_COUNTRY_CODE = "AA";
    private static final String UPDATED_COUNTRY_CODE = "BB";

    private static final String DEFAULT_TARGET_KEY = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/capture-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CaptureEventRepository captureEventRepository;

    @Autowired
    private CaptureEventMapper captureEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCaptureEventMockMvc;

    private CaptureEvent captureEvent;

    private CaptureEvent insertedCaptureEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CaptureEvent createEntity() {
        return new CaptureEvent()
            .eventType(DEFAULT_EVENT_TYPE)
            .occurredAt(DEFAULT_OCCURRED_AT)
            .occurredDate(DEFAULT_OCCURRED_DATE)
            .sessionHash(DEFAULT_SESSION_HASH)
            .locale(DEFAULT_LOCALE)
            .sourcePage(DEFAULT_SOURCE_PAGE)
            .utmSource(DEFAULT_UTM_SOURCE)
            .utmMedium(DEFAULT_UTM_MEDIUM)
            .utmCampaign(DEFAULT_UTM_CAMPAIGN)
            .referrerHost(DEFAULT_REFERRER_HOST)
            .deviceType(DEFAULT_DEVICE_TYPE)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .targetKey(DEFAULT_TARGET_KEY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CaptureEvent createUpdatedEntity() {
        return new CaptureEvent()
            .eventType(UPDATED_EVENT_TYPE)
            .occurredAt(UPDATED_OCCURRED_AT)
            .occurredDate(UPDATED_OCCURRED_DATE)
            .sessionHash(UPDATED_SESSION_HASH)
            .locale(UPDATED_LOCALE)
            .sourcePage(UPDATED_SOURCE_PAGE)
            .utmSource(UPDATED_UTM_SOURCE)
            .utmMedium(UPDATED_UTM_MEDIUM)
            .utmCampaign(UPDATED_UTM_CAMPAIGN)
            .referrerHost(UPDATED_REFERRER_HOST)
            .deviceType(UPDATED_DEVICE_TYPE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .targetKey(UPDATED_TARGET_KEY);
    }

    @BeforeEach
    void initTest() {
        captureEvent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCaptureEvent != null) {
            captureEventRepository.delete(insertedCaptureEvent);
            insertedCaptureEvent = null;
        }
    }

    @Test
    @Transactional
    void getAllCaptureEvents() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(captureEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())))
            .andExpect(jsonPath("$.[*].occurredDate").value(hasItem(DEFAULT_OCCURRED_DATE.toString())))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)))
            .andExpect(jsonPath("$.[*].locale").value(hasItem(DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].sourcePage").value(hasItem(DEFAULT_SOURCE_PAGE)))
            .andExpect(jsonPath("$.[*].utmSource").value(hasItem(DEFAULT_UTM_SOURCE)))
            .andExpect(jsonPath("$.[*].utmMedium").value(hasItem(DEFAULT_UTM_MEDIUM)))
            .andExpect(jsonPath("$.[*].utmCampaign").value(hasItem(DEFAULT_UTM_CAMPAIGN)))
            .andExpect(jsonPath("$.[*].referrerHost").value(hasItem(DEFAULT_REFERRER_HOST)))
            .andExpect(jsonPath("$.[*].deviceType").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].targetKey").value(hasItem(DEFAULT_TARGET_KEY)));
    }

    @Test
    @Transactional
    void getCaptureEvent() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get the captureEvent
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL_ID, captureEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(captureEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE.toString()))
            .andExpect(jsonPath("$.occurredAt").value(DEFAULT_OCCURRED_AT.toString()))
            .andExpect(jsonPath("$.occurredDate").value(DEFAULT_OCCURRED_DATE.toString()))
            .andExpect(jsonPath("$.sessionHash").value(DEFAULT_SESSION_HASH))
            .andExpect(jsonPath("$.locale").value(DEFAULT_LOCALE))
            .andExpect(jsonPath("$.sourcePage").value(DEFAULT_SOURCE_PAGE))
            .andExpect(jsonPath("$.utmSource").value(DEFAULT_UTM_SOURCE))
            .andExpect(jsonPath("$.utmMedium").value(DEFAULT_UTM_MEDIUM))
            .andExpect(jsonPath("$.utmCampaign").value(DEFAULT_UTM_CAMPAIGN))
            .andExpect(jsonPath("$.referrerHost").value(DEFAULT_REFERRER_HOST))
            .andExpect(jsonPath("$.deviceType").value(DEFAULT_DEVICE_TYPE.toString()))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.targetKey").value(DEFAULT_TARGET_KEY));
    }

    @Test
    @Transactional
    void getCaptureEventsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        Long id = captureEvent.getId();

        defaultCaptureEventFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCaptureEventFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCaptureEventFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByEventTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where eventType equals to
        defaultCaptureEventFiltering("eventType.equals=" + DEFAULT_EVENT_TYPE, "eventType.equals=" + UPDATED_EVENT_TYPE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByEventTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where eventType in
        defaultCaptureEventFiltering("eventType.in=" + DEFAULT_EVENT_TYPE + "," + UPDATED_EVENT_TYPE, "eventType.in=" + UPDATED_EVENT_TYPE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByEventTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where eventType is not null
        defaultCaptureEventFiltering("eventType.specified=true", "eventType.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredAt equals to
        defaultCaptureEventFiltering("occurredAt.equals=" + DEFAULT_OCCURRED_AT, "occurredAt.equals=" + UPDATED_OCCURRED_AT);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredAt in
        defaultCaptureEventFiltering(
            "occurredAt.in=" + DEFAULT_OCCURRED_AT + "," + UPDATED_OCCURRED_AT,
            "occurredAt.in=" + UPDATED_OCCURRED_AT
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredAt is not null
        defaultCaptureEventFiltering("occurredAt.specified=true", "occurredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate equals to
        defaultCaptureEventFiltering("occurredDate.equals=" + DEFAULT_OCCURRED_DATE, "occurredDate.equals=" + UPDATED_OCCURRED_DATE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate in
        defaultCaptureEventFiltering(
            "occurredDate.in=" + DEFAULT_OCCURRED_DATE + "," + UPDATED_OCCURRED_DATE,
            "occurredDate.in=" + UPDATED_OCCURRED_DATE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate is not null
        defaultCaptureEventFiltering("occurredDate.specified=true", "occurredDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate is greater than or equal to
        defaultCaptureEventFiltering(
            "occurredDate.greaterThanOrEqual=" + DEFAULT_OCCURRED_DATE,
            "occurredDate.greaterThanOrEqual=" + UPDATED_OCCURRED_DATE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate is less than or equal to
        defaultCaptureEventFiltering(
            "occurredDate.lessThanOrEqual=" + DEFAULT_OCCURRED_DATE,
            "occurredDate.lessThanOrEqual=" + SMALLER_OCCURRED_DATE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate is less than
        defaultCaptureEventFiltering("occurredDate.lessThan=" + UPDATED_OCCURRED_DATE, "occurredDate.lessThan=" + DEFAULT_OCCURRED_DATE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByOccurredDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where occurredDate is greater than
        defaultCaptureEventFiltering(
            "occurredDate.greaterThan=" + SMALLER_OCCURRED_DATE,
            "occurredDate.greaterThan=" + DEFAULT_OCCURRED_DATE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySessionHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sessionHash equals to
        defaultCaptureEventFiltering("sessionHash.equals=" + DEFAULT_SESSION_HASH, "sessionHash.equals=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySessionHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sessionHash in
        defaultCaptureEventFiltering(
            "sessionHash.in=" + DEFAULT_SESSION_HASH + "," + UPDATED_SESSION_HASH,
            "sessionHash.in=" + UPDATED_SESSION_HASH
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySessionHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sessionHash is not null
        defaultCaptureEventFiltering("sessionHash.specified=true", "sessionHash.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySessionHashContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sessionHash contains
        defaultCaptureEventFiltering("sessionHash.contains=" + DEFAULT_SESSION_HASH, "sessionHash.contains=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySessionHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sessionHash does not contain
        defaultCaptureEventFiltering(
            "sessionHash.doesNotContain=" + UPDATED_SESSION_HASH,
            "sessionHash.doesNotContain=" + DEFAULT_SESSION_HASH
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByLocaleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where locale equals to
        defaultCaptureEventFiltering("locale.equals=" + DEFAULT_LOCALE, "locale.equals=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByLocaleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where locale in
        defaultCaptureEventFiltering("locale.in=" + DEFAULT_LOCALE + "," + UPDATED_LOCALE, "locale.in=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByLocaleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where locale is not null
        defaultCaptureEventFiltering("locale.specified=true", "locale.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByLocaleContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where locale contains
        defaultCaptureEventFiltering("locale.contains=" + DEFAULT_LOCALE, "locale.contains=" + UPDATED_LOCALE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByLocaleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where locale does not contain
        defaultCaptureEventFiltering("locale.doesNotContain=" + UPDATED_LOCALE, "locale.doesNotContain=" + DEFAULT_LOCALE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySourcePageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sourcePage equals to
        defaultCaptureEventFiltering("sourcePage.equals=" + DEFAULT_SOURCE_PAGE, "sourcePage.equals=" + UPDATED_SOURCE_PAGE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySourcePageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sourcePage in
        defaultCaptureEventFiltering(
            "sourcePage.in=" + DEFAULT_SOURCE_PAGE + "," + UPDATED_SOURCE_PAGE,
            "sourcePage.in=" + UPDATED_SOURCE_PAGE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySourcePageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sourcePage is not null
        defaultCaptureEventFiltering("sourcePage.specified=true", "sourcePage.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySourcePageContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sourcePage contains
        defaultCaptureEventFiltering("sourcePage.contains=" + DEFAULT_SOURCE_PAGE, "sourcePage.contains=" + UPDATED_SOURCE_PAGE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsBySourcePageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where sourcePage does not contain
        defaultCaptureEventFiltering(
            "sourcePage.doesNotContain=" + UPDATED_SOURCE_PAGE,
            "sourcePage.doesNotContain=" + DEFAULT_SOURCE_PAGE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmSourceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmSource equals to
        defaultCaptureEventFiltering("utmSource.equals=" + DEFAULT_UTM_SOURCE, "utmSource.equals=" + UPDATED_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmSourceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmSource in
        defaultCaptureEventFiltering("utmSource.in=" + DEFAULT_UTM_SOURCE + "," + UPDATED_UTM_SOURCE, "utmSource.in=" + UPDATED_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmSourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmSource is not null
        defaultCaptureEventFiltering("utmSource.specified=true", "utmSource.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmSourceContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmSource contains
        defaultCaptureEventFiltering("utmSource.contains=" + DEFAULT_UTM_SOURCE, "utmSource.contains=" + UPDATED_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmSourceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmSource does not contain
        defaultCaptureEventFiltering("utmSource.doesNotContain=" + UPDATED_UTM_SOURCE, "utmSource.doesNotContain=" + DEFAULT_UTM_SOURCE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmMediumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmMedium equals to
        defaultCaptureEventFiltering("utmMedium.equals=" + DEFAULT_UTM_MEDIUM, "utmMedium.equals=" + UPDATED_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmMediumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmMedium in
        defaultCaptureEventFiltering("utmMedium.in=" + DEFAULT_UTM_MEDIUM + "," + UPDATED_UTM_MEDIUM, "utmMedium.in=" + UPDATED_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmMediumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmMedium is not null
        defaultCaptureEventFiltering("utmMedium.specified=true", "utmMedium.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmMediumContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmMedium contains
        defaultCaptureEventFiltering("utmMedium.contains=" + DEFAULT_UTM_MEDIUM, "utmMedium.contains=" + UPDATED_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmMediumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmMedium does not contain
        defaultCaptureEventFiltering("utmMedium.doesNotContain=" + UPDATED_UTM_MEDIUM, "utmMedium.doesNotContain=" + DEFAULT_UTM_MEDIUM);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmCampaignIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmCampaign equals to
        defaultCaptureEventFiltering("utmCampaign.equals=" + DEFAULT_UTM_CAMPAIGN, "utmCampaign.equals=" + UPDATED_UTM_CAMPAIGN);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmCampaignIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmCampaign in
        defaultCaptureEventFiltering(
            "utmCampaign.in=" + DEFAULT_UTM_CAMPAIGN + "," + UPDATED_UTM_CAMPAIGN,
            "utmCampaign.in=" + UPDATED_UTM_CAMPAIGN
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmCampaignIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmCampaign is not null
        defaultCaptureEventFiltering("utmCampaign.specified=true", "utmCampaign.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmCampaignContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmCampaign contains
        defaultCaptureEventFiltering("utmCampaign.contains=" + DEFAULT_UTM_CAMPAIGN, "utmCampaign.contains=" + UPDATED_UTM_CAMPAIGN);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByUtmCampaignNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where utmCampaign does not contain
        defaultCaptureEventFiltering(
            "utmCampaign.doesNotContain=" + UPDATED_UTM_CAMPAIGN,
            "utmCampaign.doesNotContain=" + DEFAULT_UTM_CAMPAIGN
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByReferrerHostIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where referrerHost equals to
        defaultCaptureEventFiltering("referrerHost.equals=" + DEFAULT_REFERRER_HOST, "referrerHost.equals=" + UPDATED_REFERRER_HOST);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByReferrerHostIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where referrerHost in
        defaultCaptureEventFiltering(
            "referrerHost.in=" + DEFAULT_REFERRER_HOST + "," + UPDATED_REFERRER_HOST,
            "referrerHost.in=" + UPDATED_REFERRER_HOST
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByReferrerHostIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where referrerHost is not null
        defaultCaptureEventFiltering("referrerHost.specified=true", "referrerHost.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByReferrerHostContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where referrerHost contains
        defaultCaptureEventFiltering("referrerHost.contains=" + DEFAULT_REFERRER_HOST, "referrerHost.contains=" + UPDATED_REFERRER_HOST);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByReferrerHostNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where referrerHost does not contain
        defaultCaptureEventFiltering(
            "referrerHost.doesNotContain=" + UPDATED_REFERRER_HOST,
            "referrerHost.doesNotContain=" + DEFAULT_REFERRER_HOST
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByDeviceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where deviceType equals to
        defaultCaptureEventFiltering("deviceType.equals=" + DEFAULT_DEVICE_TYPE, "deviceType.equals=" + UPDATED_DEVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByDeviceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where deviceType in
        defaultCaptureEventFiltering(
            "deviceType.in=" + DEFAULT_DEVICE_TYPE + "," + UPDATED_DEVICE_TYPE,
            "deviceType.in=" + UPDATED_DEVICE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByDeviceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where deviceType is not null
        defaultCaptureEventFiltering("deviceType.specified=true", "deviceType.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where countryCode equals to
        defaultCaptureEventFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where countryCode in
        defaultCaptureEventFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where countryCode is not null
        defaultCaptureEventFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where countryCode contains
        defaultCaptureEventFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where countryCode does not contain
        defaultCaptureEventFiltering(
            "countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE,
            "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllCaptureEventsByTargetKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where targetKey equals to
        defaultCaptureEventFiltering("targetKey.equals=" + DEFAULT_TARGET_KEY, "targetKey.equals=" + UPDATED_TARGET_KEY);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByTargetKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where targetKey in
        defaultCaptureEventFiltering("targetKey.in=" + DEFAULT_TARGET_KEY + "," + UPDATED_TARGET_KEY, "targetKey.in=" + UPDATED_TARGET_KEY);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByTargetKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where targetKey is not null
        defaultCaptureEventFiltering("targetKey.specified=true", "targetKey.specified=false");
    }

    @Test
    @Transactional
    void getAllCaptureEventsByTargetKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where targetKey contains
        defaultCaptureEventFiltering("targetKey.contains=" + DEFAULT_TARGET_KEY, "targetKey.contains=" + UPDATED_TARGET_KEY);
    }

    @Test
    @Transactional
    void getAllCaptureEventsByTargetKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCaptureEvent = captureEventRepository.saveAndFlush(captureEvent);

        // Get all the captureEventList where targetKey does not contain
        defaultCaptureEventFiltering("targetKey.doesNotContain=" + UPDATED_TARGET_KEY, "targetKey.doesNotContain=" + DEFAULT_TARGET_KEY);
    }

    private void defaultCaptureEventFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCaptureEventShouldBeFound(shouldBeFound);
        defaultCaptureEventShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCaptureEventShouldBeFound(String filter) throws Exception {
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(captureEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].occurredAt").value(hasItem(DEFAULT_OCCURRED_AT.toString())))
            .andExpect(jsonPath("$.[*].occurredDate").value(hasItem(DEFAULT_OCCURRED_DATE.toString())))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)))
            .andExpect(jsonPath("$.[*].locale").value(hasItem(DEFAULT_LOCALE)))
            .andExpect(jsonPath("$.[*].sourcePage").value(hasItem(DEFAULT_SOURCE_PAGE)))
            .andExpect(jsonPath("$.[*].utmSource").value(hasItem(DEFAULT_UTM_SOURCE)))
            .andExpect(jsonPath("$.[*].utmMedium").value(hasItem(DEFAULT_UTM_MEDIUM)))
            .andExpect(jsonPath("$.[*].utmCampaign").value(hasItem(DEFAULT_UTM_CAMPAIGN)))
            .andExpect(jsonPath("$.[*].referrerHost").value(hasItem(DEFAULT_REFERRER_HOST)))
            .andExpect(jsonPath("$.[*].deviceType").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].targetKey").value(hasItem(DEFAULT_TARGET_KEY)));

        // Check, that the count call also returns 1
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCaptureEventShouldNotBeFound(String filter) throws Exception {
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCaptureEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCaptureEvent() throws Exception {
        // Get the captureEvent
        restCaptureEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    protected long getRepositoryCount() {
        return captureEventRepository.count();
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

    protected CaptureEvent getPersistedCaptureEvent(CaptureEvent captureEvent) {
        return captureEventRepository.findById(captureEvent.getId()).orElseThrow();
    }

    protected void assertPersistedCaptureEventToMatchAllProperties(CaptureEvent expectedCaptureEvent) {
        assertCaptureEventAllPropertiesEquals(expectedCaptureEvent, getPersistedCaptureEvent(expectedCaptureEvent));
    }

    protected void assertPersistedCaptureEventToMatchUpdatableProperties(CaptureEvent expectedCaptureEvent) {
        assertCaptureEventAllUpdatablePropertiesEquals(expectedCaptureEvent, getPersistedCaptureEvent(expectedCaptureEvent));
    }
}
