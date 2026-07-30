package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.ServiceHighlightAsserts.*;
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
import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import net.jojoaddison.abofonsa.preview.domain.ServiceHighlight;
import net.jojoaddison.abofonsa.preview.repository.ServiceHighlightRepository;
import net.jojoaddison.abofonsa.preview.service.ServiceHighlightService;
import net.jojoaddison.abofonsa.preview.service.dto.ServiceHighlightDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.ServiceHighlightMapper;
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
 * Integration tests for the {@link ServiceHighlightResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ServiceHighlightResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final String ENTITY_API_URL = "/api/service-highlights";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceHighlightRepository serviceHighlightRepository;

    @Mock
    private ServiceHighlightRepository serviceHighlightRepositoryMock;

    @Autowired
    private ServiceHighlightMapper serviceHighlightMapper;

    @Mock
    private ServiceHighlightService serviceHighlightServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceHighlightMockMvc;

    private ServiceHighlight serviceHighlight;

    private ServiceHighlight insertedServiceHighlight;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceHighlight createEntity(EntityManager em) {
        ServiceHighlight serviceHighlight = new ServiceHighlight().label(DEFAULT_LABEL).displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        CareServiceTeaser careServiceTeaser;
        if (TestUtil.findAll(em, CareServiceTeaser.class).isEmpty()) {
            careServiceTeaser = CareServiceTeaserResourceIT.createEntity();
            em.persist(careServiceTeaser);
            em.flush();
        } else {
            careServiceTeaser = TestUtil.findAll(em, CareServiceTeaser.class).get(0);
        }
        serviceHighlight.setService(careServiceTeaser);
        return serviceHighlight;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceHighlight createUpdatedEntity(EntityManager em) {
        ServiceHighlight updatedServiceHighlight = new ServiceHighlight().label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        CareServiceTeaser careServiceTeaser;
        if (TestUtil.findAll(em, CareServiceTeaser.class).isEmpty()) {
            careServiceTeaser = CareServiceTeaserResourceIT.createUpdatedEntity();
            em.persist(careServiceTeaser);
            em.flush();
        } else {
            careServiceTeaser = TestUtil.findAll(em, CareServiceTeaser.class).get(0);
        }
        updatedServiceHighlight.setService(careServiceTeaser);
        return updatedServiceHighlight;
    }

    @BeforeEach
    void initTest() {
        serviceHighlight = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceHighlight != null) {
            serviceHighlightRepository.delete(insertedServiceHighlight);
            insertedServiceHighlight = null;
        }
    }

    @Test
    @Transactional
    void createServiceHighlight() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);
        var returnedServiceHighlightDTO = om.readValue(
            restServiceHighlightMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHighlightDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceHighlightDTO.class
        );

        // Validate the ServiceHighlight in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceHighlight = serviceHighlightMapper.toEntity(returnedServiceHighlightDTO);
        assertServiceHighlightUpdatableFieldsEquals(returnedServiceHighlight, getPersistedServiceHighlight(returnedServiceHighlight));

        insertedServiceHighlight = returnedServiceHighlight;
    }

    @Test
    @Transactional
    void createServiceHighlightWithExistingId() throws Exception {
        // Create the ServiceHighlight with an existing ID
        serviceHighlight.setId(1L);
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceHighlightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHighlightDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceHighlight.setLabel(null);

        // Create the ServiceHighlight, which fails.
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        restServiceHighlightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHighlightDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceHighlight.setDisplayOrder(null);

        // Create the ServiceHighlight, which fails.
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        restServiceHighlightMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHighlightDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServiceHighlights() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        // Get all the serviceHighlightList
        restServiceHighlightMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceHighlight.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceHighlightsWithEagerRelationshipsIsEnabled() throws Exception {
        when(serviceHighlightServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restServiceHighlightMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(serviceHighlightServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceHighlightsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(serviceHighlightServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restServiceHighlightMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(serviceHighlightRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getServiceHighlight() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        // Get the serviceHighlight
        restServiceHighlightMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceHighlight.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceHighlight.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingServiceHighlight() throws Exception {
        // Get the serviceHighlight
        restServiceHighlightMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServiceHighlight() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceHighlight
        ServiceHighlight updatedServiceHighlight = serviceHighlightRepository.findById(serviceHighlight.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServiceHighlight are not directly saved in db
        em.detach(updatedServiceHighlight);
        updatedServiceHighlight.label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(updatedServiceHighlight);

        restServiceHighlightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceHighlightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHighlightDTO))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceHighlightToMatchAllProperties(updatedServiceHighlight);
    }

    @Test
    @Transactional
    void putNonExistingServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceHighlightDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHighlightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHighlightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHighlightDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServiceHighlightWithPatch() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceHighlight using partial update
        ServiceHighlight partialUpdatedServiceHighlight = new ServiceHighlight();
        partialUpdatedServiceHighlight.setId(serviceHighlight.getId());

        partialUpdatedServiceHighlight.label(UPDATED_LABEL);

        restServiceHighlightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceHighlight.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceHighlight))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHighlight in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceHighlightUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceHighlight, serviceHighlight),
            getPersistedServiceHighlight(serviceHighlight)
        );
    }

    @Test
    @Transactional
    void fullUpdateServiceHighlightWithPatch() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceHighlight using partial update
        ServiceHighlight partialUpdatedServiceHighlight = new ServiceHighlight();
        partialUpdatedServiceHighlight.setId(serviceHighlight.getId());

        partialUpdatedServiceHighlight.label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);

        restServiceHighlightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceHighlight.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceHighlight))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHighlight in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceHighlightUpdatableFieldsEquals(
            partialUpdatedServiceHighlight,
            getPersistedServiceHighlight(partialUpdatedServiceHighlight)
        );
    }

    @Test
    @Transactional
    void patchNonExistingServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceHighlightDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceHighlightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceHighlightDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceHighlight() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHighlight.setId(longCount.incrementAndGet());

        // Create the ServiceHighlight
        ServiceHighlightDTO serviceHighlightDTO = serviceHighlightMapper.toDto(serviceHighlight);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHighlightMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(serviceHighlightDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceHighlight in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServiceHighlight() throws Exception {
        // Initialize the database
        insertedServiceHighlight = serviceHighlightRepository.saveAndFlush(serviceHighlight);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceHighlight
        restServiceHighlightMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceHighlight.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceHighlightRepository.count();
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

    protected ServiceHighlight getPersistedServiceHighlight(ServiceHighlight serviceHighlight) {
        return serviceHighlightRepository.findById(serviceHighlight.getId()).orElseThrow();
    }

    protected void assertPersistedServiceHighlightToMatchAllProperties(ServiceHighlight expectedServiceHighlight) {
        assertServiceHighlightAllPropertiesEquals(expectedServiceHighlight, getPersistedServiceHighlight(expectedServiceHighlight));
    }

    protected void assertPersistedServiceHighlightToMatchUpdatableProperties(ServiceHighlight expectedServiceHighlight) {
        assertServiceHighlightAllUpdatablePropertiesEquals(
            expectedServiceHighlight,
            getPersistedServiceHighlight(expectedServiceHighlight)
        );
    }
}
