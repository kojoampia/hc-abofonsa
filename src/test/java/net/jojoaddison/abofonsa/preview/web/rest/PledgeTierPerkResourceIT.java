package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierPerkAsserts.*;
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
import net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierPerkRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.PledgeTierPerkService;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierPerkDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.PledgeTierPerkMapper;
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
 * Integration tests for the {@link PledgeTierPerkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class PledgeTierPerkResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final String ENTITY_API_URL = "/api/pledge-tier-perks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PledgeTierPerkRepository pledgeTierPerkRepository;

    @Mock
    private PledgeTierPerkRepository pledgeTierPerkRepositoryMock;

    @Autowired
    private PledgeTierPerkMapper pledgeTierPerkMapper;

    @Mock
    private PledgeTierPerkService pledgeTierPerkServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPledgeTierPerkMockMvc;

    private PledgeTierPerk pledgeTierPerk;

    private PledgeTierPerk insertedPledgeTierPerk;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PledgeTierPerk createEntity(EntityManager em) {
        PledgeTierPerk pledgeTierPerk = new PledgeTierPerk().label(DEFAULT_LABEL).displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        PledgeTierTeaser pledgeTierTeaser;
        if (TestUtil.findAll(em, PledgeTierTeaser.class).isEmpty()) {
            pledgeTierTeaser = PledgeTierTeaserResourceIT.createEntity();
            em.persist(pledgeTierTeaser);
            em.flush();
        } else {
            pledgeTierTeaser = TestUtil.findAll(em, PledgeTierTeaser.class).get(0);
        }
        pledgeTierPerk.setTier(pledgeTierTeaser);
        return pledgeTierPerk;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PledgeTierPerk createUpdatedEntity(EntityManager em) {
        PledgeTierPerk updatedPledgeTierPerk = new PledgeTierPerk().label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        PledgeTierTeaser pledgeTierTeaser;
        if (TestUtil.findAll(em, PledgeTierTeaser.class).isEmpty()) {
            pledgeTierTeaser = PledgeTierTeaserResourceIT.createUpdatedEntity();
            em.persist(pledgeTierTeaser);
            em.flush();
        } else {
            pledgeTierTeaser = TestUtil.findAll(em, PledgeTierTeaser.class).get(0);
        }
        updatedPledgeTierPerk.setTier(pledgeTierTeaser);
        return updatedPledgeTierPerk;
    }

    @BeforeEach
    void initTest() {
        pledgeTierPerk = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPledgeTierPerk != null) {
            pledgeTierPerkRepository.delete(insertedPledgeTierPerk);
            insertedPledgeTierPerk = null;
        }
    }

    @Test
    @Transactional
    void createPledgeTierPerk() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);
        var returnedPledgeTierPerkDTO = om.readValue(
            restPledgeTierPerkMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierPerkDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PledgeTierPerkDTO.class
        );

        // Validate the PledgeTierPerk in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPledgeTierPerk = pledgeTierPerkMapper.toEntity(returnedPledgeTierPerkDTO);
        assertPledgeTierPerkUpdatableFieldsEquals(returnedPledgeTierPerk, getPersistedPledgeTierPerk(returnedPledgeTierPerk));

        insertedPledgeTierPerk = returnedPledgeTierPerk;
    }

    @Test
    @Transactional
    void createPledgeTierPerkWithExistingId() throws Exception {
        // Create the PledgeTierPerk with an existing ID
        pledgeTierPerk.setId(1L);
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPledgeTierPerkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierPerkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierPerk.setLabel(null);

        // Create the PledgeTierPerk, which fails.
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        restPledgeTierPerkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierPerkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierPerk.setDisplayOrder(null);

        // Create the PledgeTierPerk, which fails.
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        restPledgeTierPerkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierPerkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPledgeTierPerks() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        // Get all the pledgeTierPerkList
        restPledgeTierPerkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pledgeTierPerk.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPledgeTierPerksWithEagerRelationshipsIsEnabled() throws Exception {
        when(pledgeTierPerkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPledgeTierPerkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pledgeTierPerkServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPledgeTierPerksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pledgeTierPerkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPledgeTierPerkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pledgeTierPerkRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPledgeTierPerk() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        // Get the pledgeTierPerk
        restPledgeTierPerkMockMvc
            .perform(get(ENTITY_API_URL_ID, pledgeTierPerk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pledgeTierPerk.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingPledgeTierPerk() throws Exception {
        // Get the pledgeTierPerk
        restPledgeTierPerkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPledgeTierPerk() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierPerk
        PledgeTierPerk updatedPledgeTierPerk = pledgeTierPerkRepository.findById(pledgeTierPerk.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPledgeTierPerk are not directly saved in db
        em.detach(updatedPledgeTierPerk);
        updatedPledgeTierPerk.label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(updatedPledgeTierPerk);

        restPledgeTierPerkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pledgeTierPerkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierPerkDTO))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPledgeTierPerkToMatchAllProperties(updatedPledgeTierPerk);
    }

    @Test
    @Transactional
    void putNonExistingPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pledgeTierPerkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierPerkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierPerkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierPerkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePledgeTierPerkWithPatch() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierPerk using partial update
        PledgeTierPerk partialUpdatedPledgeTierPerk = new PledgeTierPerk();
        partialUpdatedPledgeTierPerk.setId(pledgeTierPerk.getId());

        partialUpdatedPledgeTierPerk.label(UPDATED_LABEL);

        restPledgeTierPerkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPledgeTierPerk.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPledgeTierPerk))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierPerk in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPledgeTierPerkUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPledgeTierPerk, pledgeTierPerk),
            getPersistedPledgeTierPerk(pledgeTierPerk)
        );
    }

    @Test
    @Transactional
    void fullUpdatePledgeTierPerkWithPatch() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierPerk using partial update
        PledgeTierPerk partialUpdatedPledgeTierPerk = new PledgeTierPerk();
        partialUpdatedPledgeTierPerk.setId(pledgeTierPerk.getId());

        partialUpdatedPledgeTierPerk.label(UPDATED_LABEL).displayOrder(UPDATED_DISPLAY_ORDER);

        restPledgeTierPerkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPledgeTierPerk.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPledgeTierPerk))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierPerk in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPledgeTierPerkUpdatableFieldsEquals(partialUpdatedPledgeTierPerk, getPersistedPledgeTierPerk(partialUpdatedPledgeTierPerk));
    }

    @Test
    @Transactional
    void patchNonExistingPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pledgeTierPerkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pledgeTierPerkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pledgeTierPerkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPledgeTierPerk() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierPerk.setId(longCount.incrementAndGet());

        // Create the PledgeTierPerk
        PledgeTierPerkDTO pledgeTierPerkDTO = pledgeTierPerkMapper.toDto(pledgeTierPerk);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierPerkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pledgeTierPerkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PledgeTierPerk in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePledgeTierPerk() throws Exception {
        // Initialize the database
        insertedPledgeTierPerk = pledgeTierPerkRepository.saveAndFlush(pledgeTierPerk);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pledgeTierPerk
        restPledgeTierPerkMockMvc
            .perform(delete(ENTITY_API_URL_ID, pledgeTierPerk.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pledgeTierPerkRepository.count();
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

    protected PledgeTierPerk getPersistedPledgeTierPerk(PledgeTierPerk pledgeTierPerk) {
        return pledgeTierPerkRepository.findById(pledgeTierPerk.getId()).orElseThrow();
    }

    protected void assertPersistedPledgeTierPerkToMatchAllProperties(PledgeTierPerk expectedPledgeTierPerk) {
        assertPledgeTierPerkAllPropertiesEquals(expectedPledgeTierPerk, getPersistedPledgeTierPerk(expectedPledgeTierPerk));
    }

    protected void assertPersistedPledgeTierPerkToMatchUpdatableProperties(PledgeTierPerk expectedPledgeTierPerk) {
        assertPledgeTierPerkAllUpdatablePropertiesEquals(expectedPledgeTierPerk, getPersistedPledgeTierPerk(expectedPledgeTierPerk));
    }
}
