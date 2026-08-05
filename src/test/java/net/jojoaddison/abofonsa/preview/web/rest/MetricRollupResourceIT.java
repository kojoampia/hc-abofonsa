package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.MetricRollupAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.mapper.MetricRollupMapper;
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
 * Integration tests for the {@link MetricRollupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class MetricRollupResourceIT {

    private static final MetricKey DEFAULT_METRIC_KEY = MetricKey.WAITLIST_SIGNUPS;
    private static final MetricKey UPDATED_METRIC_KEY = MetricKey.WAITLIST_CONFIRMED;

    private static final BucketType DEFAULT_BUCKET_TYPE = BucketType.HOUR;
    private static final BucketType UPDATED_BUCKET_TYPE = BucketType.DAY;

    private static final Instant DEFAULT_BUCKET_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BUCKET_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_BUCKET_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BUCKET_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DIMENSION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DIMENSION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DIMENSION_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_DIMENSION_VALUE = "BBBBBBBBBB";

    private static final Long DEFAULT_VALUE = 0L;
    private static final Long UPDATED_VALUE = 1L;
    private static final Long SMALLER_VALUE = 0L - 1L;

    private static final Instant DEFAULT_COMPUTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPUTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/metric-rollups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricRollupRepository metricRollupRepository;

    @Autowired
    private MetricRollupMapper metricRollupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMetricRollupMockMvc;

    private MetricRollup metricRollup;

    private MetricRollup insertedMetricRollup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricRollup createEntity() {
        return new MetricRollup()
            .metricKey(DEFAULT_METRIC_KEY)
            .bucketType(DEFAULT_BUCKET_TYPE)
            .bucketStart(DEFAULT_BUCKET_START)
            .bucketEnd(DEFAULT_BUCKET_END)
            .dimensionName(DEFAULT_DIMENSION_NAME)
            .dimensionValue(DEFAULT_DIMENSION_VALUE)
            .value(DEFAULT_VALUE)
            .computedAt(DEFAULT_COMPUTED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricRollup createUpdatedEntity() {
        return new MetricRollup()
            .metricKey(UPDATED_METRIC_KEY)
            .bucketType(UPDATED_BUCKET_TYPE)
            .bucketStart(UPDATED_BUCKET_START)
            .bucketEnd(UPDATED_BUCKET_END)
            .dimensionName(UPDATED_DIMENSION_NAME)
            .dimensionValue(UPDATED_DIMENSION_VALUE)
            .value(UPDATED_VALUE)
            .computedAt(UPDATED_COMPUTED_AT);
    }

    @BeforeEach
    void initTest() {
        metricRollup = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMetricRollup != null) {
            metricRollupRepository.delete(insertedMetricRollup);
            insertedMetricRollup = null;
        }
    }

    @Test
    @Transactional
    void getAllMetricRollups() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metricRollup.getId().intValue())))
            .andExpect(jsonPath("$.[*].metricKey").value(hasItem(DEFAULT_METRIC_KEY.toString())))
            .andExpect(jsonPath("$.[*].bucketType").value(hasItem(DEFAULT_BUCKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].bucketStart").value(hasItem(DEFAULT_BUCKET_START.toString())))
            .andExpect(jsonPath("$.[*].bucketEnd").value(hasItem(DEFAULT_BUCKET_END.toString())))
            .andExpect(jsonPath("$.[*].dimensionName").value(hasItem(DEFAULT_DIMENSION_NAME)))
            .andExpect(jsonPath("$.[*].dimensionValue").value(hasItem(DEFAULT_DIMENSION_VALUE)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].computedAt").value(hasItem(DEFAULT_COMPUTED_AT.toString())));
    }

    @Test
    @Transactional
    void getMetricRollup() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get the metricRollup
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL_ID, metricRollup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(metricRollup.getId().intValue()))
            .andExpect(jsonPath("$.metricKey").value(DEFAULT_METRIC_KEY.toString()))
            .andExpect(jsonPath("$.bucketType").value(DEFAULT_BUCKET_TYPE.toString()))
            .andExpect(jsonPath("$.bucketStart").value(DEFAULT_BUCKET_START.toString()))
            .andExpect(jsonPath("$.bucketEnd").value(DEFAULT_BUCKET_END.toString()))
            .andExpect(jsonPath("$.dimensionName").value(DEFAULT_DIMENSION_NAME))
            .andExpect(jsonPath("$.dimensionValue").value(DEFAULT_DIMENSION_VALUE))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()))
            .andExpect(jsonPath("$.computedAt").value(DEFAULT_COMPUTED_AT.toString()));
    }

    @Test
    @Transactional
    void getMetricRollupsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        Long id = metricRollup.getId();

        defaultMetricRollupFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMetricRollupFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMetricRollupFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByMetricKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where metricKey equals to
        defaultMetricRollupFiltering("metricKey.equals=" + DEFAULT_METRIC_KEY, "metricKey.equals=" + UPDATED_METRIC_KEY);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByMetricKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where metricKey in
        defaultMetricRollupFiltering("metricKey.in=" + DEFAULT_METRIC_KEY + "," + UPDATED_METRIC_KEY, "metricKey.in=" + UPDATED_METRIC_KEY);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByMetricKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where metricKey is not null
        defaultMetricRollupFiltering("metricKey.specified=true", "metricKey.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketType equals to
        defaultMetricRollupFiltering("bucketType.equals=" + DEFAULT_BUCKET_TYPE, "bucketType.equals=" + UPDATED_BUCKET_TYPE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketType in
        defaultMetricRollupFiltering(
            "bucketType.in=" + DEFAULT_BUCKET_TYPE + "," + UPDATED_BUCKET_TYPE,
            "bucketType.in=" + UPDATED_BUCKET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketType is not null
        defaultMetricRollupFiltering("bucketType.specified=true", "bucketType.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketStartIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketStart equals to
        defaultMetricRollupFiltering("bucketStart.equals=" + DEFAULT_BUCKET_START, "bucketStart.equals=" + UPDATED_BUCKET_START);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketStartIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketStart in
        defaultMetricRollupFiltering(
            "bucketStart.in=" + DEFAULT_BUCKET_START + "," + UPDATED_BUCKET_START,
            "bucketStart.in=" + UPDATED_BUCKET_START
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketStart is not null
        defaultMetricRollupFiltering("bucketStart.specified=true", "bucketStart.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketEndIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketEnd equals to
        defaultMetricRollupFiltering("bucketEnd.equals=" + DEFAULT_BUCKET_END, "bucketEnd.equals=" + UPDATED_BUCKET_END);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketEndIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketEnd in
        defaultMetricRollupFiltering("bucketEnd.in=" + DEFAULT_BUCKET_END + "," + UPDATED_BUCKET_END, "bucketEnd.in=" + UPDATED_BUCKET_END);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByBucketEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where bucketEnd is not null
        defaultMetricRollupFiltering("bucketEnd.specified=true", "bucketEnd.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionName equals to
        defaultMetricRollupFiltering("dimensionName.equals=" + DEFAULT_DIMENSION_NAME, "dimensionName.equals=" + UPDATED_DIMENSION_NAME);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionName in
        defaultMetricRollupFiltering(
            "dimensionName.in=" + DEFAULT_DIMENSION_NAME + "," + UPDATED_DIMENSION_NAME,
            "dimensionName.in=" + UPDATED_DIMENSION_NAME
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionName is not null
        defaultMetricRollupFiltering("dimensionName.specified=true", "dimensionName.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionName contains
        defaultMetricRollupFiltering(
            "dimensionName.contains=" + DEFAULT_DIMENSION_NAME,
            "dimensionName.contains=" + UPDATED_DIMENSION_NAME
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionName does not contain
        defaultMetricRollupFiltering(
            "dimensionName.doesNotContain=" + UPDATED_DIMENSION_NAME,
            "dimensionName.doesNotContain=" + DEFAULT_DIMENSION_NAME
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionValue equals to
        defaultMetricRollupFiltering(
            "dimensionValue.equals=" + DEFAULT_DIMENSION_VALUE,
            "dimensionValue.equals=" + UPDATED_DIMENSION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionValue in
        defaultMetricRollupFiltering(
            "dimensionValue.in=" + DEFAULT_DIMENSION_VALUE + "," + UPDATED_DIMENSION_VALUE,
            "dimensionValue.in=" + UPDATED_DIMENSION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionValue is not null
        defaultMetricRollupFiltering("dimensionValue.specified=true", "dimensionValue.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionValueContainsSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionValue contains
        defaultMetricRollupFiltering(
            "dimensionValue.contains=" + DEFAULT_DIMENSION_VALUE,
            "dimensionValue.contains=" + UPDATED_DIMENSION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByDimensionValueNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where dimensionValue does not contain
        defaultMetricRollupFiltering(
            "dimensionValue.doesNotContain=" + UPDATED_DIMENSION_VALUE,
            "dimensionValue.doesNotContain=" + DEFAULT_DIMENSION_VALUE
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value equals to
        defaultMetricRollupFiltering("value.equals=" + DEFAULT_VALUE, "value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value in
        defaultMetricRollupFiltering("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE, "value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value is not null
        defaultMetricRollupFiltering("value.specified=true", "value.specified=false");
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value is greater than or equal to
        defaultMetricRollupFiltering("value.greaterThanOrEqual=" + DEFAULT_VALUE, "value.greaterThanOrEqual=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value is less than or equal to
        defaultMetricRollupFiltering("value.lessThanOrEqual=" + DEFAULT_VALUE, "value.lessThanOrEqual=" + SMALLER_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value is less than
        defaultMetricRollupFiltering("value.lessThan=" + UPDATED_VALUE, "value.lessThan=" + DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where value is greater than
        defaultMetricRollupFiltering("value.greaterThan=" + SMALLER_VALUE, "value.greaterThan=" + DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByComputedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where computedAt equals to
        defaultMetricRollupFiltering("computedAt.equals=" + DEFAULT_COMPUTED_AT, "computedAt.equals=" + UPDATED_COMPUTED_AT);
    }

    @Test
    @Transactional
    void getAllMetricRollupsByComputedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where computedAt in
        defaultMetricRollupFiltering(
            "computedAt.in=" + DEFAULT_COMPUTED_AT + "," + UPDATED_COMPUTED_AT,
            "computedAt.in=" + UPDATED_COMPUTED_AT
        );
    }

    @Test
    @Transactional
    void getAllMetricRollupsByComputedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMetricRollup = metricRollupRepository.saveAndFlush(metricRollup);

        // Get all the metricRollupList where computedAt is not null
        defaultMetricRollupFiltering("computedAt.specified=true", "computedAt.specified=false");
    }

    private void defaultMetricRollupFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMetricRollupShouldBeFound(shouldBeFound);
        defaultMetricRollupShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMetricRollupShouldBeFound(String filter) throws Exception {
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(metricRollup.getId().intValue())))
            .andExpect(jsonPath("$.[*].metricKey").value(hasItem(DEFAULT_METRIC_KEY.toString())))
            .andExpect(jsonPath("$.[*].bucketType").value(hasItem(DEFAULT_BUCKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].bucketStart").value(hasItem(DEFAULT_BUCKET_START.toString())))
            .andExpect(jsonPath("$.[*].bucketEnd").value(hasItem(DEFAULT_BUCKET_END.toString())))
            .andExpect(jsonPath("$.[*].dimensionName").value(hasItem(DEFAULT_DIMENSION_NAME)))
            .andExpect(jsonPath("$.[*].dimensionValue").value(hasItem(DEFAULT_DIMENSION_VALUE)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].computedAt").value(hasItem(DEFAULT_COMPUTED_AT.toString())));

        // Check, that the count call also returns 1
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMetricRollupShouldNotBeFound(String filter) throws Exception {
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMetricRollupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMetricRollup() throws Exception {
        // Get the metricRollup
        restMetricRollupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    protected long getRepositoryCount() {
        return metricRollupRepository.count();
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

    protected MetricRollup getPersistedMetricRollup(MetricRollup metricRollup) {
        return metricRollupRepository.findById(metricRollup.getId()).orElseThrow();
    }

    protected void assertPersistedMetricRollupToMatchAllProperties(MetricRollup expectedMetricRollup) {
        assertMetricRollupAllPropertiesEquals(expectedMetricRollup, getPersistedMetricRollup(expectedMetricRollup));
    }

    protected void assertPersistedMetricRollupToMatchUpdatableProperties(MetricRollup expectedMetricRollup) {
        assertMetricRollupAllUpdatablePropertiesEquals(expectedMetricRollup, getPersistedMetricRollup(expectedMetricRollup));
    }
}
