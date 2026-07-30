package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.LaunchSettingAsserts.*;
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
import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import net.jojoaddison.abofonsa.preview.repository.LaunchSettingRepository;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchSettingDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.LaunchSettingMapper;
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
 * Integration tests for the {@link LaunchSettingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LaunchSettingResourceIT {

    private static final String DEFAULT_SETTING_KEY = "AAAAAAAAAA";
    private static final String UPDATED_SETTING_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_ORGANISATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANISATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TAGLINE = "AAAAAAAAAA";
    private static final String UPDATED_TAGLINE = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAUNCH_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAUNCH_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAUNCH_TIMEZONE = "AAAAAAAAAA";
    private static final String UPDATED_LAUNCH_TIMEZONE = "BBBBBBBBBB";

    private static final String DEFAULT_FUND_URL = "AAAAAAAAAA";
    private static final String UPDATED_FUND_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_OFFICE_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_OFFICE_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PARENT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PARENT_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PARENT_COMPANY_URL = "AAAAAAAAAA";
    private static final String UPDATED_PARENT_COMPANY_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/launch-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LaunchSettingRepository launchSettingRepository;

    @Autowired
    private LaunchSettingMapper launchSettingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaunchSettingMockMvc;

    private LaunchSetting launchSetting;

    private LaunchSetting insertedLaunchSetting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaunchSetting createEntity() {
        return new LaunchSetting()
            .settingKey(DEFAULT_SETTING_KEY)
            .organisationName(DEFAULT_ORGANISATION_NAME)
            .tagline(DEFAULT_TAGLINE)
            .launchAt(DEFAULT_LAUNCH_AT)
            .launchTimezone(DEFAULT_LAUNCH_TIMEZONE)
            .fundUrl(DEFAULT_FUND_URL)
            .contactEmail(DEFAULT_CONTACT_EMAIL)
            .contactPhone(DEFAULT_CONTACT_PHONE)
            .officeAddress(DEFAULT_OFFICE_ADDRESS)
            .parentCompanyName(DEFAULT_PARENT_COMPANY_NAME)
            .parentCompanyUrl(DEFAULT_PARENT_COMPANY_URL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaunchSetting createUpdatedEntity() {
        return new LaunchSetting()
            .settingKey(UPDATED_SETTING_KEY)
            .organisationName(UPDATED_ORGANISATION_NAME)
            .tagline(UPDATED_TAGLINE)
            .launchAt(UPDATED_LAUNCH_AT)
            .launchTimezone(UPDATED_LAUNCH_TIMEZONE)
            .fundUrl(UPDATED_FUND_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .parentCompanyName(UPDATED_PARENT_COMPANY_NAME)
            .parentCompanyUrl(UPDATED_PARENT_COMPANY_URL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        launchSetting = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLaunchSetting != null) {
            launchSettingRepository.delete(insertedLaunchSetting);
            insertedLaunchSetting = null;
        }
    }

    @Test
    @Transactional
    void createLaunchSetting() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);
        var returnedLaunchSettingDTO = om.readValue(
            restLaunchSettingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LaunchSettingDTO.class
        );

        // Validate the LaunchSetting in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLaunchSetting = launchSettingMapper.toEntity(returnedLaunchSettingDTO);
        assertLaunchSettingUpdatableFieldsEquals(returnedLaunchSetting, getPersistedLaunchSetting(returnedLaunchSetting));

        insertedLaunchSetting = returnedLaunchSetting;
    }

    @Test
    @Transactional
    void createLaunchSettingWithExistingId() throws Exception {
        // Create the LaunchSetting with an existing ID
        launchSetting.setId(1L);
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSettingKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setSettingKey(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrganisationNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setOrganisationName(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLaunchAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setLaunchAt(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLaunchTimezoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setLaunchTimezone(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFundUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setFundUrl(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContactEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setContactEmail(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchSetting.setActive(null);

        // Create the LaunchSetting, which fails.
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        restLaunchSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLaunchSettings() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        // Get all the launchSettingList
        restLaunchSettingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(launchSetting.getId().intValue())))
            .andExpect(jsonPath("$.[*].settingKey").value(hasItem(DEFAULT_SETTING_KEY)))
            .andExpect(jsonPath("$.[*].organisationName").value(hasItem(DEFAULT_ORGANISATION_NAME)))
            .andExpect(jsonPath("$.[*].tagline").value(hasItem(DEFAULT_TAGLINE)))
            .andExpect(jsonPath("$.[*].launchAt").value(hasItem(DEFAULT_LAUNCH_AT.toString())))
            .andExpect(jsonPath("$.[*].launchTimezone").value(hasItem(DEFAULT_LAUNCH_TIMEZONE)))
            .andExpect(jsonPath("$.[*].fundUrl").value(hasItem(DEFAULT_FUND_URL)))
            .andExpect(jsonPath("$.[*].contactEmail").value(hasItem(DEFAULT_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPhone").value(hasItem(DEFAULT_CONTACT_PHONE)))
            .andExpect(jsonPath("$.[*].officeAddress").value(hasItem(DEFAULT_OFFICE_ADDRESS)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getLaunchSetting() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        // Get the launchSetting
        restLaunchSettingMockMvc
            .perform(get(ENTITY_API_URL_ID, launchSetting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(launchSetting.getId().intValue()))
            .andExpect(jsonPath("$.settingKey").value(DEFAULT_SETTING_KEY))
            .andExpect(jsonPath("$.organisationName").value(DEFAULT_ORGANISATION_NAME))
            .andExpect(jsonPath("$.tagline").value(DEFAULT_TAGLINE))
            .andExpect(jsonPath("$.launchAt").value(DEFAULT_LAUNCH_AT.toString()))
            .andExpect(jsonPath("$.launchTimezone").value(DEFAULT_LAUNCH_TIMEZONE))
            .andExpect(jsonPath("$.fundUrl").value(DEFAULT_FUND_URL))
            .andExpect(jsonPath("$.contactEmail").value(DEFAULT_CONTACT_EMAIL))
            .andExpect(jsonPath("$.contactPhone").value(DEFAULT_CONTACT_PHONE))
            .andExpect(jsonPath("$.officeAddress").value(DEFAULT_OFFICE_ADDRESS))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingLaunchSetting() throws Exception {
        // Get the launchSetting
        restLaunchSettingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLaunchSetting() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchSetting
        LaunchSetting updatedLaunchSetting = launchSettingRepository.findById(launchSetting.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLaunchSetting are not directly saved in db
        em.detach(updatedLaunchSetting);
        updatedLaunchSetting
            .settingKey(UPDATED_SETTING_KEY)
            .organisationName(UPDATED_ORGANISATION_NAME)
            .tagline(UPDATED_TAGLINE)
            .launchAt(UPDATED_LAUNCH_AT)
            .launchTimezone(UPDATED_LAUNCH_TIMEZONE)
            .fundUrl(UPDATED_FUND_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .parentCompanyName(UPDATED_PARENT_COMPANY_NAME)
            .parentCompanyUrl(UPDATED_PARENT_COMPANY_URL)
            .active(UPDATED_ACTIVE);
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(updatedLaunchSetting);

        restLaunchSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, launchSettingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchSettingDTO))
            )
            .andExpect(status().isOk());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLaunchSettingToMatchAllProperties(updatedLaunchSetting);
    }

    @Test
    @Transactional
    void putNonExistingLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, launchSettingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLaunchSettingWithPatch() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchSetting using partial update
        LaunchSetting partialUpdatedLaunchSetting = new LaunchSetting();
        partialUpdatedLaunchSetting.setId(launchSetting.getId());

        partialUpdatedLaunchSetting.officeAddress(UPDATED_OFFICE_ADDRESS);

        restLaunchSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaunchSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLaunchSetting))
            )
            .andExpect(status().isOk());

        // Validate the LaunchSetting in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLaunchSettingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLaunchSetting, launchSetting),
            getPersistedLaunchSetting(launchSetting)
        );
    }

    @Test
    @Transactional
    void fullUpdateLaunchSettingWithPatch() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchSetting using partial update
        LaunchSetting partialUpdatedLaunchSetting = new LaunchSetting();
        partialUpdatedLaunchSetting.setId(launchSetting.getId());

        partialUpdatedLaunchSetting
            .settingKey(UPDATED_SETTING_KEY)
            .organisationName(UPDATED_ORGANISATION_NAME)
            .tagline(UPDATED_TAGLINE)
            .launchAt(UPDATED_LAUNCH_AT)
            .launchTimezone(UPDATED_LAUNCH_TIMEZONE)
            .fundUrl(UPDATED_FUND_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .parentCompanyName(UPDATED_PARENT_COMPANY_NAME)
            .parentCompanyUrl(UPDATED_PARENT_COMPANY_URL)
            .active(UPDATED_ACTIVE);

        restLaunchSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaunchSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLaunchSetting))
            )
            .andExpect(status().isOk());

        // Validate the LaunchSetting in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLaunchSettingUpdatableFieldsEquals(partialUpdatedLaunchSetting, getPersistedLaunchSetting(partialUpdatedLaunchSetting));
    }

    @Test
    @Transactional
    void patchNonExistingLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, launchSettingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(launchSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(launchSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLaunchSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchSetting.setId(longCount.incrementAndGet());

        // Create the LaunchSetting
        LaunchSettingDTO launchSettingDTO = launchSettingMapper.toDto(launchSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchSettingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(launchSettingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaunchSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLaunchSetting() throws Exception {
        // Initialize the database
        insertedLaunchSetting = launchSettingRepository.saveAndFlush(launchSetting);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the launchSetting
        restLaunchSettingMockMvc
            .perform(delete(ENTITY_API_URL_ID, launchSetting.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return launchSettingRepository.count();
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

    protected LaunchSetting getPersistedLaunchSetting(LaunchSetting launchSetting) {
        return launchSettingRepository.findById(launchSetting.getId()).orElseThrow();
    }

    protected void assertPersistedLaunchSettingToMatchAllProperties(LaunchSetting expectedLaunchSetting) {
        assertLaunchSettingAllPropertiesEquals(expectedLaunchSetting, getPersistedLaunchSetting(expectedLaunchSetting));
    }

    protected void assertPersistedLaunchSettingToMatchUpdatableProperties(LaunchSetting expectedLaunchSetting) {
        assertLaunchSettingAllUpdatablePropertiesEquals(expectedLaunchSetting, getPersistedLaunchSetting(expectedLaunchSetting));
    }
}
