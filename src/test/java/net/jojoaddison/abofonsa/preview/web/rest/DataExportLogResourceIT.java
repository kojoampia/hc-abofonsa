package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.DataExportLogAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportFormat;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportKind;
import net.jojoaddison.abofonsa.preview.repository.DataExportLogRepository;
import net.jojoaddison.abofonsa.preview.service.mapper.DataExportLogMapper;
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
 * Integration tests for the {@link DataExportLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DataExportLogResourceIT {

    private static final ExportKind DEFAULT_EXPORT_KIND = ExportKind.WAITLIST_EMAILS;
    private static final ExportKind UPDATED_EXPORT_KIND = ExportKind.CAPTURE_EVENTS;

    private static final ExportFormat DEFAULT_FORMAT = ExportFormat.CSV;
    private static final ExportFormat UPDATED_FORMAT = ExportFormat.JSON;

    private static final Instant DEFAULT_RANGE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RANGE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RANGE_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RANGE_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BucketType DEFAULT_BUCKET_TYPE = BucketType.HOUR;
    private static final BucketType UPDATED_BUCKET_TYPE = BucketType.DAY;

    private static final String DEFAULT_FILTER_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_FILTER_SUMMARY = "BBBBBBBBBB";

    private static final Integer DEFAULT_ROW_COUNT = 0;
    private static final Integer UPDATED_ROW_COUNT = 1;
    private static final Integer SMALLER_ROW_COUNT = 0 - 1;

    private static final String DEFAULT_REQUESTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUESTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_DURATION_MS = 1L;
    private static final Long UPDATED_DURATION_MS = 2L;
    private static final Long SMALLER_DURATION_MS = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/data-export-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DataExportLogRepository dataExportLogRepository;

    @Autowired
    private DataExportLogMapper dataExportLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDataExportLogMockMvc;

    private DataExportLog dataExportLog;

    private DataExportLog insertedDataExportLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataExportLog createEntity() {
        return new DataExportLog()
            .exportKind(DEFAULT_EXPORT_KIND)
            .format(DEFAULT_FORMAT)
            .rangeFrom(DEFAULT_RANGE_FROM)
            .rangeTo(DEFAULT_RANGE_TO)
            .bucketType(DEFAULT_BUCKET_TYPE)
            .filterSummary(DEFAULT_FILTER_SUMMARY)
            .rowCount(DEFAULT_ROW_COUNT)
            .requestedBy(DEFAULT_REQUESTED_BY)
            .requestedAt(DEFAULT_REQUESTED_AT)
            .durationMs(DEFAULT_DURATION_MS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataExportLog createUpdatedEntity() {
        return new DataExportLog()
            .exportKind(UPDATED_EXPORT_KIND)
            .format(UPDATED_FORMAT)
            .rangeFrom(UPDATED_RANGE_FROM)
            .rangeTo(UPDATED_RANGE_TO)
            .bucketType(UPDATED_BUCKET_TYPE)
            .filterSummary(UPDATED_FILTER_SUMMARY)
            .rowCount(UPDATED_ROW_COUNT)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedAt(UPDATED_REQUESTED_AT)
            .durationMs(UPDATED_DURATION_MS);
    }

    @BeforeEach
    void initTest() {
        dataExportLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDataExportLog != null) {
            dataExportLogRepository.delete(insertedDataExportLog);
            insertedDataExportLog = null;
        }
    }

    @Test
    @Transactional
    void getAllDataExportLogs() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataExportLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].exportKind").value(hasItem(DEFAULT_EXPORT_KIND.toString())))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].rangeFrom").value(hasItem(DEFAULT_RANGE_FROM.toString())))
            .andExpect(jsonPath("$.[*].rangeTo").value(hasItem(DEFAULT_RANGE_TO.toString())))
            .andExpect(jsonPath("$.[*].bucketType").value(hasItem(DEFAULT_BUCKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].filterSummary").value(hasItem(DEFAULT_FILTER_SUMMARY)))
            .andExpect(jsonPath("$.[*].rowCount").value(hasItem(DEFAULT_ROW_COUNT)))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedAt").value(hasItem(DEFAULT_REQUESTED_AT.toString())))
            .andExpect(jsonPath("$.[*].durationMs").value(hasItem(DEFAULT_DURATION_MS.intValue())));
    }

    @Test
    @Transactional
    void getDataExportLog() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get the dataExportLog
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL_ID, dataExportLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dataExportLog.getId().intValue()))
            .andExpect(jsonPath("$.exportKind").value(DEFAULT_EXPORT_KIND.toString()))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.rangeFrom").value(DEFAULT_RANGE_FROM.toString()))
            .andExpect(jsonPath("$.rangeTo").value(DEFAULT_RANGE_TO.toString()))
            .andExpect(jsonPath("$.bucketType").value(DEFAULT_BUCKET_TYPE.toString()))
            .andExpect(jsonPath("$.filterSummary").value(DEFAULT_FILTER_SUMMARY))
            .andExpect(jsonPath("$.rowCount").value(DEFAULT_ROW_COUNT))
            .andExpect(jsonPath("$.requestedBy").value(DEFAULT_REQUESTED_BY))
            .andExpect(jsonPath("$.requestedAt").value(DEFAULT_REQUESTED_AT.toString()))
            .andExpect(jsonPath("$.durationMs").value(DEFAULT_DURATION_MS.intValue()));
    }

    @Test
    @Transactional
    void getDataExportLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        Long id = dataExportLog.getId();

        defaultDataExportLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDataExportLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDataExportLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByExportKindIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where exportKind equals to
        defaultDataExportLogFiltering("exportKind.equals=" + DEFAULT_EXPORT_KIND, "exportKind.equals=" + UPDATED_EXPORT_KIND);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByExportKindIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where exportKind in
        defaultDataExportLogFiltering(
            "exportKind.in=" + DEFAULT_EXPORT_KIND + "," + UPDATED_EXPORT_KIND,
            "exportKind.in=" + UPDATED_EXPORT_KIND
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByExportKindIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where exportKind is not null
        defaultDataExportLogFiltering("exportKind.specified=true", "exportKind.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFormatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where format equals to
        defaultDataExportLogFiltering("format.equals=" + DEFAULT_FORMAT, "format.equals=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFormatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where format in
        defaultDataExportLogFiltering("format.in=" + DEFAULT_FORMAT + "," + UPDATED_FORMAT, "format.in=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFormatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where format is not null
        defaultDataExportLogFiltering("format.specified=true", "format.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeFrom equals to
        defaultDataExportLogFiltering("rangeFrom.equals=" + DEFAULT_RANGE_FROM, "rangeFrom.equals=" + UPDATED_RANGE_FROM);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeFrom in
        defaultDataExportLogFiltering(
            "rangeFrom.in=" + DEFAULT_RANGE_FROM + "," + UPDATED_RANGE_FROM,
            "rangeFrom.in=" + UPDATED_RANGE_FROM
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeFrom is not null
        defaultDataExportLogFiltering("rangeFrom.specified=true", "rangeFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeTo equals to
        defaultDataExportLogFiltering("rangeTo.equals=" + DEFAULT_RANGE_TO, "rangeTo.equals=" + UPDATED_RANGE_TO);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeTo in
        defaultDataExportLogFiltering("rangeTo.in=" + DEFAULT_RANGE_TO + "," + UPDATED_RANGE_TO, "rangeTo.in=" + UPDATED_RANGE_TO);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRangeToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rangeTo is not null
        defaultDataExportLogFiltering("rangeTo.specified=true", "rangeTo.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByBucketTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where bucketType equals to
        defaultDataExportLogFiltering("bucketType.equals=" + DEFAULT_BUCKET_TYPE, "bucketType.equals=" + UPDATED_BUCKET_TYPE);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByBucketTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where bucketType in
        defaultDataExportLogFiltering(
            "bucketType.in=" + DEFAULT_BUCKET_TYPE + "," + UPDATED_BUCKET_TYPE,
            "bucketType.in=" + UPDATED_BUCKET_TYPE
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByBucketTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where bucketType is not null
        defaultDataExportLogFiltering("bucketType.specified=true", "bucketType.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFilterSummaryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where filterSummary equals to
        defaultDataExportLogFiltering("filterSummary.equals=" + DEFAULT_FILTER_SUMMARY, "filterSummary.equals=" + UPDATED_FILTER_SUMMARY);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFilterSummaryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where filterSummary in
        defaultDataExportLogFiltering(
            "filterSummary.in=" + DEFAULT_FILTER_SUMMARY + "," + UPDATED_FILTER_SUMMARY,
            "filterSummary.in=" + UPDATED_FILTER_SUMMARY
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFilterSummaryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where filterSummary is not null
        defaultDataExportLogFiltering("filterSummary.specified=true", "filterSummary.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFilterSummaryContainsSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where filterSummary contains
        defaultDataExportLogFiltering(
            "filterSummary.contains=" + DEFAULT_FILTER_SUMMARY,
            "filterSummary.contains=" + UPDATED_FILTER_SUMMARY
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByFilterSummaryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where filterSummary does not contain
        defaultDataExportLogFiltering(
            "filterSummary.doesNotContain=" + UPDATED_FILTER_SUMMARY,
            "filterSummary.doesNotContain=" + DEFAULT_FILTER_SUMMARY
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount equals to
        defaultDataExportLogFiltering("rowCount.equals=" + DEFAULT_ROW_COUNT, "rowCount.equals=" + UPDATED_ROW_COUNT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount in
        defaultDataExportLogFiltering("rowCount.in=" + DEFAULT_ROW_COUNT + "," + UPDATED_ROW_COUNT, "rowCount.in=" + UPDATED_ROW_COUNT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount is not null
        defaultDataExportLogFiltering("rowCount.specified=true", "rowCount.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount is greater than or equal to
        defaultDataExportLogFiltering(
            "rowCount.greaterThanOrEqual=" + DEFAULT_ROW_COUNT,
            "rowCount.greaterThanOrEqual=" + UPDATED_ROW_COUNT
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount is less than or equal to
        defaultDataExportLogFiltering("rowCount.lessThanOrEqual=" + DEFAULT_ROW_COUNT, "rowCount.lessThanOrEqual=" + SMALLER_ROW_COUNT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount is less than
        defaultDataExportLogFiltering("rowCount.lessThan=" + UPDATED_ROW_COUNT, "rowCount.lessThan=" + DEFAULT_ROW_COUNT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRowCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where rowCount is greater than
        defaultDataExportLogFiltering("rowCount.greaterThan=" + SMALLER_ROW_COUNT, "rowCount.greaterThan=" + DEFAULT_ROW_COUNT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedBy equals to
        defaultDataExportLogFiltering("requestedBy.equals=" + DEFAULT_REQUESTED_BY, "requestedBy.equals=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedBy in
        defaultDataExportLogFiltering(
            "requestedBy.in=" + DEFAULT_REQUESTED_BY + "," + UPDATED_REQUESTED_BY,
            "requestedBy.in=" + UPDATED_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedBy is not null
        defaultDataExportLogFiltering("requestedBy.specified=true", "requestedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedByContainsSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedBy contains
        defaultDataExportLogFiltering("requestedBy.contains=" + DEFAULT_REQUESTED_BY, "requestedBy.contains=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedBy does not contain
        defaultDataExportLogFiltering(
            "requestedBy.doesNotContain=" + UPDATED_REQUESTED_BY,
            "requestedBy.doesNotContain=" + DEFAULT_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedAt equals to
        defaultDataExportLogFiltering("requestedAt.equals=" + DEFAULT_REQUESTED_AT, "requestedAt.equals=" + UPDATED_REQUESTED_AT);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedAt in
        defaultDataExportLogFiltering(
            "requestedAt.in=" + DEFAULT_REQUESTED_AT + "," + UPDATED_REQUESTED_AT,
            "requestedAt.in=" + UPDATED_REQUESTED_AT
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByRequestedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where requestedAt is not null
        defaultDataExportLogFiltering("requestedAt.specified=true", "requestedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs equals to
        defaultDataExportLogFiltering("durationMs.equals=" + DEFAULT_DURATION_MS, "durationMs.equals=" + UPDATED_DURATION_MS);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs in
        defaultDataExportLogFiltering(
            "durationMs.in=" + DEFAULT_DURATION_MS + "," + UPDATED_DURATION_MS,
            "durationMs.in=" + UPDATED_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs is not null
        defaultDataExportLogFiltering("durationMs.specified=true", "durationMs.specified=false");
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs is greater than or equal to
        defaultDataExportLogFiltering(
            "durationMs.greaterThanOrEqual=" + DEFAULT_DURATION_MS,
            "durationMs.greaterThanOrEqual=" + UPDATED_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs is less than or equal to
        defaultDataExportLogFiltering(
            "durationMs.lessThanOrEqual=" + DEFAULT_DURATION_MS,
            "durationMs.lessThanOrEqual=" + SMALLER_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs is less than
        defaultDataExportLogFiltering("durationMs.lessThan=" + UPDATED_DURATION_MS, "durationMs.lessThan=" + DEFAULT_DURATION_MS);
    }

    @Test
    @Transactional
    void getAllDataExportLogsByDurationMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDataExportLog = dataExportLogRepository.saveAndFlush(dataExportLog);

        // Get all the dataExportLogList where durationMs is greater than
        defaultDataExportLogFiltering("durationMs.greaterThan=" + SMALLER_DURATION_MS, "durationMs.greaterThan=" + DEFAULT_DURATION_MS);
    }

    private void defaultDataExportLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDataExportLogShouldBeFound(shouldBeFound);
        defaultDataExportLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDataExportLogShouldBeFound(String filter) throws Exception {
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataExportLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].exportKind").value(hasItem(DEFAULT_EXPORT_KIND.toString())))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].rangeFrom").value(hasItem(DEFAULT_RANGE_FROM.toString())))
            .andExpect(jsonPath("$.[*].rangeTo").value(hasItem(DEFAULT_RANGE_TO.toString())))
            .andExpect(jsonPath("$.[*].bucketType").value(hasItem(DEFAULT_BUCKET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].filterSummary").value(hasItem(DEFAULT_FILTER_SUMMARY)))
            .andExpect(jsonPath("$.[*].rowCount").value(hasItem(DEFAULT_ROW_COUNT)))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedAt").value(hasItem(DEFAULT_REQUESTED_AT.toString())))
            .andExpect(jsonPath("$.[*].durationMs").value(hasItem(DEFAULT_DURATION_MS.intValue())));

        // Check, that the count call also returns 1
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDataExportLogShouldNotBeFound(String filter) throws Exception {
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDataExportLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDataExportLog() throws Exception {
        // Get the dataExportLog
        restDataExportLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    protected long getRepositoryCount() {
        return dataExportLogRepository.count();
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

    protected DataExportLog getPersistedDataExportLog(DataExportLog dataExportLog) {
        return dataExportLogRepository.findById(dataExportLog.getId()).orElseThrow();
    }

    protected void assertPersistedDataExportLogToMatchAllProperties(DataExportLog expectedDataExportLog) {
        assertDataExportLogAllPropertiesEquals(expectedDataExportLog, getPersistedDataExportLog(expectedDataExportLog));
    }

    protected void assertPersistedDataExportLogToMatchUpdatableProperties(DataExportLog expectedDataExportLog) {
        assertDataExportLogAllUpdatablePropertiesEquals(expectedDataExportLog, getPersistedDataExportLog(expectedDataExportLog));
    }
}
