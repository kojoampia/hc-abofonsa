package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.LaunchMilestoneAsserts.*;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.LaunchMilestone;
import net.jojoaddison.abofonsa.preview.repository.LaunchMilestoneRepository;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchMilestoneDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.LaunchMilestoneMapper;
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
 * Integration tests for the {@link LaunchMilestoneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LaunchMilestoneResourceIT {

    private static final String DEFAULT_PHASE_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_PHASE_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_MILESTONE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MILESTONE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_CURRENT = false;
    private static final Boolean UPDATED_CURRENT = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final String ENTITY_API_URL = "/api/launch-milestones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LaunchMilestoneRepository launchMilestoneRepository;

    @Autowired
    private LaunchMilestoneMapper launchMilestoneMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaunchMilestoneMockMvc;

    private LaunchMilestone launchMilestone;

    private LaunchMilestone insertedLaunchMilestone;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaunchMilestone createEntity() {
        return new LaunchMilestone()
            .phaseLabel(DEFAULT_PHASE_LABEL)
            .title(DEFAULT_TITLE)
            .body(DEFAULT_BODY)
            .milestoneDate(DEFAULT_MILESTONE_DATE)
            .current(DEFAULT_CURRENT)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .published(DEFAULT_PUBLISHED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaunchMilestone createUpdatedEntity() {
        return new LaunchMilestone()
            .phaseLabel(UPDATED_PHASE_LABEL)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .milestoneDate(UPDATED_MILESTONE_DATE)
            .current(UPDATED_CURRENT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
    }

    @BeforeEach
    void initTest() {
        launchMilestone = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLaunchMilestone != null) {
            launchMilestoneRepository.delete(insertedLaunchMilestone);
            insertedLaunchMilestone = null;
        }
    }

    @Test
    @Transactional
    void createLaunchMilestone() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);
        var returnedLaunchMilestoneDTO = om.readValue(
            restLaunchMilestoneMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LaunchMilestoneDTO.class
        );

        // Validate the LaunchMilestone in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLaunchMilestone = launchMilestoneMapper.toEntity(returnedLaunchMilestoneDTO);
        assertLaunchMilestoneUpdatableFieldsEquals(returnedLaunchMilestone, getPersistedLaunchMilestone(returnedLaunchMilestone));

        insertedLaunchMilestone = returnedLaunchMilestone;
    }

    @Test
    @Transactional
    void createLaunchMilestoneWithExistingId() throws Exception {
        // Create the LaunchMilestone with an existing ID
        launchMilestone.setId(1L);
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhaseLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchMilestone.setPhaseLabel(null);

        // Create the LaunchMilestone, which fails.
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchMilestone.setTitle(null);

        // Create the LaunchMilestone, which fails.
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchMilestone.setCurrent(null);

        // Create the LaunchMilestone, which fails.
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchMilestone.setDisplayOrder(null);

        // Create the LaunchMilestone, which fails.
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        launchMilestone.setPublished(null);

        // Create the LaunchMilestone, which fails.
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        restLaunchMilestoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLaunchMilestones() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        // Get all the launchMilestoneList
        restLaunchMilestoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(launchMilestone.getId().intValue())))
            .andExpect(jsonPath("$.[*].phaseLabel").value(hasItem(DEFAULT_PHASE_LABEL)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].milestoneDate").value(hasItem(DEFAULT_MILESTONE_DATE.toString())))
            .andExpect(jsonPath("$.[*].current").value(hasItem(DEFAULT_CURRENT)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)));
    }

    @Test
    @Transactional
    void getLaunchMilestone() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        // Get the launchMilestone
        restLaunchMilestoneMockMvc
            .perform(get(ENTITY_API_URL_ID, launchMilestone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(launchMilestone.getId().intValue()))
            .andExpect(jsonPath("$.phaseLabel").value(DEFAULT_PHASE_LABEL))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.milestoneDate").value(DEFAULT_MILESTONE_DATE.toString()))
            .andExpect(jsonPath("$.current").value(DEFAULT_CURRENT))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED));
    }

    @Test
    @Transactional
    void getNonExistingLaunchMilestone() throws Exception {
        // Get the launchMilestone
        restLaunchMilestoneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLaunchMilestone() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchMilestone
        LaunchMilestone updatedLaunchMilestone = launchMilestoneRepository.findById(launchMilestone.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLaunchMilestone are not directly saved in db
        em.detach(updatedLaunchMilestone);
        updatedLaunchMilestone
            .phaseLabel(UPDATED_PHASE_LABEL)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .milestoneDate(UPDATED_MILESTONE_DATE)
            .current(UPDATED_CURRENT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(updatedLaunchMilestone);

        restLaunchMilestoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, launchMilestoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchMilestoneDTO))
            )
            .andExpect(status().isOk());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLaunchMilestoneToMatchAllProperties(updatedLaunchMilestone);
    }

    @Test
    @Transactional
    void putNonExistingLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, launchMilestoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchMilestoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(launchMilestoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLaunchMilestoneWithPatch() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchMilestone using partial update
        LaunchMilestone partialUpdatedLaunchMilestone = new LaunchMilestone();
        partialUpdatedLaunchMilestone.setId(launchMilestone.getId());

        partialUpdatedLaunchMilestone.milestoneDate(UPDATED_MILESTONE_DATE).current(UPDATED_CURRENT).published(UPDATED_PUBLISHED);

        restLaunchMilestoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaunchMilestone.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLaunchMilestone))
            )
            .andExpect(status().isOk());

        // Validate the LaunchMilestone in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLaunchMilestoneUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLaunchMilestone, launchMilestone),
            getPersistedLaunchMilestone(launchMilestone)
        );
    }

    @Test
    @Transactional
    void fullUpdateLaunchMilestoneWithPatch() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the launchMilestone using partial update
        LaunchMilestone partialUpdatedLaunchMilestone = new LaunchMilestone();
        partialUpdatedLaunchMilestone.setId(launchMilestone.getId());

        partialUpdatedLaunchMilestone
            .phaseLabel(UPDATED_PHASE_LABEL)
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .milestoneDate(UPDATED_MILESTONE_DATE)
            .current(UPDATED_CURRENT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);

        restLaunchMilestoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLaunchMilestone.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLaunchMilestone))
            )
            .andExpect(status().isOk());

        // Validate the LaunchMilestone in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLaunchMilestoneUpdatableFieldsEquals(
            partialUpdatedLaunchMilestone,
            getPersistedLaunchMilestone(partialUpdatedLaunchMilestone)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, launchMilestoneDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(launchMilestoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(launchMilestoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLaunchMilestone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        launchMilestone.setId(longCount.incrementAndGet());

        // Create the LaunchMilestone
        LaunchMilestoneDTO launchMilestoneDTO = launchMilestoneMapper.toDto(launchMilestone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLaunchMilestoneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(launchMilestoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LaunchMilestone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLaunchMilestone() throws Exception {
        // Initialize the database
        insertedLaunchMilestone = launchMilestoneRepository.saveAndFlush(launchMilestone);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the launchMilestone
        restLaunchMilestoneMockMvc
            .perform(delete(ENTITY_API_URL_ID, launchMilestone.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return launchMilestoneRepository.count();
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

    protected LaunchMilestone getPersistedLaunchMilestone(LaunchMilestone launchMilestone) {
        return launchMilestoneRepository.findById(launchMilestone.getId()).orElseThrow();
    }

    protected void assertPersistedLaunchMilestoneToMatchAllProperties(LaunchMilestone expectedLaunchMilestone) {
        assertLaunchMilestoneAllPropertiesEquals(expectedLaunchMilestone, getPersistedLaunchMilestone(expectedLaunchMilestone));
    }

    protected void assertPersistedLaunchMilestoneToMatchUpdatableProperties(LaunchMilestone expectedLaunchMilestone) {
        assertLaunchMilestoneAllUpdatablePropertiesEquals(expectedLaunchMilestone, getPersistedLaunchMilestone(expectedLaunchMilestone));
    }
}
