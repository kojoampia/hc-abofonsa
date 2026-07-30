package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.PlanFeatureAsserts.*;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import net.jojoaddison.abofonsa.preview.domain.PlanFeature;
import net.jojoaddison.abofonsa.preview.repository.PlanFeatureRepository;
import net.jojoaddison.abofonsa.preview.service.PlanFeatureService;
import net.jojoaddison.abofonsa.preview.service.dto.PlanFeatureDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.PlanFeatureMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlanFeatureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlanFeatureResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_INCLUDED = false;
    private static final Boolean UPDATED_INCLUDED = true;

    private static final Boolean DEFAULT_EMPHASISED = false;
    private static final Boolean UPDATED_EMPHASISED = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final String ENTITY_API_URL = "/api/plan-features";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanFeatureRepository planFeatureRepository;

    @Mock
    private PlanFeatureRepository planFeatureRepositoryMock;

    @Autowired
    private PlanFeatureMapper planFeatureMapper;

    @Mock
    private PlanFeatureService planFeatureServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanFeatureMockMvc;

    private PlanFeature planFeature;

    private PlanFeature insertedPlanFeature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFeature createEntity(EntityManager em) {
        PlanFeature planFeature = new PlanFeature()
            .label(DEFAULT_LABEL)
            .included(DEFAULT_INCLUDED)
            .emphasised(DEFAULT_EMPHASISED)
            .displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        CarePlanTeaser carePlanTeaser;
        if (TestUtil.findAll(em, CarePlanTeaser.class).isEmpty()) {
            carePlanTeaser = CarePlanTeaserResourceIT.createEntity();
            em.persist(carePlanTeaser);
            em.flush();
        } else {
            carePlanTeaser = TestUtil.findAll(em, CarePlanTeaser.class).get(0);
        }
        planFeature.setPlan(carePlanTeaser);
        return planFeature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanFeature createUpdatedEntity(EntityManager em) {
        PlanFeature updatedPlanFeature = new PlanFeature()
            .label(UPDATED_LABEL)
            .included(UPDATED_INCLUDED)
            .emphasised(UPDATED_EMPHASISED)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        CarePlanTeaser carePlanTeaser;
        if (TestUtil.findAll(em, CarePlanTeaser.class).isEmpty()) {
            carePlanTeaser = CarePlanTeaserResourceIT.createUpdatedEntity();
            em.persist(carePlanTeaser);
            em.flush();
        } else {
            carePlanTeaser = TestUtil.findAll(em, CarePlanTeaser.class).get(0);
        }
        updatedPlanFeature.setPlan(carePlanTeaser);
        return updatedPlanFeature;
    }

    @BeforeEach
    void initTest() {
        planFeature = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanFeature != null) {
            planFeatureRepository.delete(insertedPlanFeature);
            insertedPlanFeature = null;
        }
    }

    @Test
    @Transactional
    void createPlanFeature() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);
        var returnedPlanFeatureDTO = om.readValue(
            restPlanFeatureMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlanFeatureDTO.class
        );

        // Validate the PlanFeature in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanFeature = planFeatureMapper.toEntity(returnedPlanFeatureDTO);
        assertPlanFeatureUpdatableFieldsEquals(returnedPlanFeature, getPersistedPlanFeature(returnedPlanFeature));

        insertedPlanFeature = returnedPlanFeature;
    }

    @Test
    @Transactional
    void createPlanFeatureWithExistingId() throws Exception {
        // Create the PlanFeature with an existing ID
        planFeature.setId(1L);
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setLabel(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIncludedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setIncluded(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmphasisedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setEmphasised(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planFeature.setDisplayOrder(null);

        // Create the PlanFeature, which fails.
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        restPlanFeatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlanFeatures() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        // Get all the planFeatureList
        restPlanFeatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(planFeature.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].included").value(hasItem(DEFAULT_INCLUDED)))
            .andExpect(jsonPath("$.[*].emphasised").value(hasItem(DEFAULT_EMPHASISED)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanFeaturesWithEagerRelationshipsIsEnabled() throws Exception {
        when(planFeatureServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlanFeatureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(planFeatureServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanFeaturesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(planFeatureServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlanFeatureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(planFeatureRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        // Get the planFeature
        restPlanFeatureMockMvc
            .perform(get(ENTITY_API_URL_ID, planFeature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(planFeature.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.included").value(DEFAULT_INCLUDED))
            .andExpect(jsonPath("$.emphasised").value(DEFAULT_EMPHASISED))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingPlanFeature() throws Exception {
        // Get the planFeature
        restPlanFeatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature
        PlanFeature updatedPlanFeature = planFeatureRepository.findById(planFeature.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlanFeature are not directly saved in db
        em.detach(updatedPlanFeature);
        updatedPlanFeature
            .label(UPDATED_LABEL)
            .included(UPDATED_INCLUDED)
            .emphasised(UPDATED_EMPHASISED)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(updatedPlanFeature);

        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanFeatureToMatchAllProperties(updatedPlanFeature);
    }

    @Test
    @Transactional
    void putNonExistingPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanFeatureWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature using partial update
        PlanFeature partialUpdatedPlanFeature = new PlanFeature();
        partialUpdatedPlanFeature.setId(planFeature.getId());

        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFeature))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFeatureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanFeature, planFeature),
            getPersistedPlanFeature(planFeature)
        );
    }

    @Test
    @Transactional
    void fullUpdatePlanFeatureWithPatch() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planFeature using partial update
        PlanFeature partialUpdatedPlanFeature = new PlanFeature();
        partialUpdatedPlanFeature.setId(planFeature.getId());

        partialUpdatedPlanFeature
            .label(UPDATED_LABEL)
            .included(UPDATED_INCLUDED)
            .emphasised(UPDATED_EMPHASISED)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanFeature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlanFeature))
            )
            .andExpect(status().isOk());

        // Validate the PlanFeature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanFeatureUpdatableFieldsEquals(partialUpdatedPlanFeature, getPersistedPlanFeature(partialUpdatedPlanFeature));
    }

    @Test
    @Transactional
    void patchNonExistingPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, planFeatureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planFeatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlanFeature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planFeature.setId(longCount.incrementAndGet());

        // Create the PlanFeature
        PlanFeatureDTO planFeatureDTO = planFeatureMapper.toDto(planFeature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanFeatureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(planFeatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlanFeature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlanFeature() throws Exception {
        // Initialize the database
        insertedPlanFeature = planFeatureRepository.saveAndFlush(planFeature);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planFeature
        restPlanFeatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, planFeature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planFeatureRepository.count();
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

    protected PlanFeature getPersistedPlanFeature(PlanFeature planFeature) {
        return planFeatureRepository.findById(planFeature.getId()).orElseThrow();
    }

    protected void assertPersistedPlanFeatureToMatchAllProperties(PlanFeature expectedPlanFeature) {
        assertPlanFeatureAllPropertiesEquals(expectedPlanFeature, getPersistedPlanFeature(expectedPlanFeature));
    }

    protected void assertPersistedPlanFeatureToMatchUpdatableProperties(PlanFeature expectedPlanFeature) {
        assertPlanFeatureAllUpdatablePropertiesEquals(expectedPlanFeature, getPersistedPlanFeature(expectedPlanFeature));
    }
}
