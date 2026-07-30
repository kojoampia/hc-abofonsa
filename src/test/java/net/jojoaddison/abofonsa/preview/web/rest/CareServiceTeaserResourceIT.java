package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.CareServiceTeaserAsserts.*;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import net.jojoaddison.abofonsa.preview.repository.CareServiceTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CareServiceTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CareServiceTeaserMapper;
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
 * Integration tests for the {@link CareServiceTeaserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CareServiceTeaserResourceIT {

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BLURB = "AAAAAAAAAA";
    private static final String UPDATED_BLURB = "BBBBBBBBBB";

    private static final String DEFAULT_ICON_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ICON_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_AVAILABLE_ON = "AAAAAAAAAA";
    private static final String UPDATED_AVAILABLE_ON = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final String ENTITY_API_URL = "/api/care-service-teasers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CareServiceTeaserRepository careServiceTeaserRepository;

    @Autowired
    private CareServiceTeaserMapper careServiceTeaserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCareServiceTeaserMockMvc;

    private CareServiceTeaser careServiceTeaser;

    private CareServiceTeaser insertedCareServiceTeaser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CareServiceTeaser createEntity() {
        return new CareServiceTeaser()
            .slug(DEFAULT_SLUG)
            .name(DEFAULT_NAME)
            .blurb(DEFAULT_BLURB)
            .iconKey(DEFAULT_ICON_KEY)
            .availableOn(DEFAULT_AVAILABLE_ON)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .published(DEFAULT_PUBLISHED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CareServiceTeaser createUpdatedEntity() {
        return new CareServiceTeaser()
            .slug(UPDATED_SLUG)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .iconKey(UPDATED_ICON_KEY)
            .availableOn(UPDATED_AVAILABLE_ON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
    }

    @BeforeEach
    void initTest() {
        careServiceTeaser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCareServiceTeaser != null) {
            careServiceTeaserRepository.delete(insertedCareServiceTeaser);
            insertedCareServiceTeaser = null;
        }
    }

    @Test
    @Transactional
    void createCareServiceTeaser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);
        var returnedCareServiceTeaserDTO = om.readValue(
            restCareServiceTeaserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CareServiceTeaserDTO.class
        );

        // Validate the CareServiceTeaser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCareServiceTeaser = careServiceTeaserMapper.toEntity(returnedCareServiceTeaserDTO);
        assertCareServiceTeaserUpdatableFieldsEquals(returnedCareServiceTeaser, getPersistedCareServiceTeaser(returnedCareServiceTeaser));

        insertedCareServiceTeaser = returnedCareServiceTeaser;
    }

    @Test
    @Transactional
    void createCareServiceTeaserWithExistingId() throws Exception {
        // Create the CareServiceTeaser with an existing ID
        careServiceTeaser.setId(1L);
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCareServiceTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        careServiceTeaser.setSlug(null);

        // Create the CareServiceTeaser, which fails.
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        restCareServiceTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        careServiceTeaser.setName(null);

        // Create the CareServiceTeaser, which fails.
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        restCareServiceTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        careServiceTeaser.setDisplayOrder(null);

        // Create the CareServiceTeaser, which fails.
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        restCareServiceTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        careServiceTeaser.setPublished(null);

        // Create the CareServiceTeaser, which fails.
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        restCareServiceTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCareServiceTeasers() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        // Get all the careServiceTeaserList
        restCareServiceTeaserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(careServiceTeaser.getId().intValue())))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].blurb").value(hasItem(DEFAULT_BLURB)))
            .andExpect(jsonPath("$.[*].iconKey").value(hasItem(DEFAULT_ICON_KEY)))
            .andExpect(jsonPath("$.[*].availableOn").value(hasItem(DEFAULT_AVAILABLE_ON)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)));
    }

    @Test
    @Transactional
    void getCareServiceTeaser() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        // Get the careServiceTeaser
        restCareServiceTeaserMockMvc
            .perform(get(ENTITY_API_URL_ID, careServiceTeaser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(careServiceTeaser.getId().intValue()))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.blurb").value(DEFAULT_BLURB))
            .andExpect(jsonPath("$.iconKey").value(DEFAULT_ICON_KEY))
            .andExpect(jsonPath("$.availableOn").value(DEFAULT_AVAILABLE_ON))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED));
    }

    @Test
    @Transactional
    void getNonExistingCareServiceTeaser() throws Exception {
        // Get the careServiceTeaser
        restCareServiceTeaserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCareServiceTeaser() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the careServiceTeaser
        CareServiceTeaser updatedCareServiceTeaser = careServiceTeaserRepository.findById(careServiceTeaser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCareServiceTeaser are not directly saved in db
        em.detach(updatedCareServiceTeaser);
        updatedCareServiceTeaser
            .slug(UPDATED_SLUG)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .iconKey(UPDATED_ICON_KEY)
            .availableOn(UPDATED_AVAILABLE_ON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(updatedCareServiceTeaser);

        restCareServiceTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, careServiceTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(careServiceTeaserDTO))
            )
            .andExpect(status().isOk());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCareServiceTeaserToMatchAllProperties(updatedCareServiceTeaser);
    }

    @Test
    @Transactional
    void putNonExistingCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, careServiceTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(careServiceTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(careServiceTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCareServiceTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the careServiceTeaser using partial update
        CareServiceTeaser partialUpdatedCareServiceTeaser = new CareServiceTeaser();
        partialUpdatedCareServiceTeaser.setId(careServiceTeaser.getId());

        partialUpdatedCareServiceTeaser.iconKey(UPDATED_ICON_KEY).published(UPDATED_PUBLISHED);

        restCareServiceTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCareServiceTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCareServiceTeaser))
            )
            .andExpect(status().isOk());

        // Validate the CareServiceTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCareServiceTeaserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCareServiceTeaser, careServiceTeaser),
            getPersistedCareServiceTeaser(careServiceTeaser)
        );
    }

    @Test
    @Transactional
    void fullUpdateCareServiceTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the careServiceTeaser using partial update
        CareServiceTeaser partialUpdatedCareServiceTeaser = new CareServiceTeaser();
        partialUpdatedCareServiceTeaser.setId(careServiceTeaser.getId());

        partialUpdatedCareServiceTeaser
            .slug(UPDATED_SLUG)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .iconKey(UPDATED_ICON_KEY)
            .availableOn(UPDATED_AVAILABLE_ON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);

        restCareServiceTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCareServiceTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCareServiceTeaser))
            )
            .andExpect(status().isOk());

        // Validate the CareServiceTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCareServiceTeaserUpdatableFieldsEquals(
            partialUpdatedCareServiceTeaser,
            getPersistedCareServiceTeaser(partialUpdatedCareServiceTeaser)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, careServiceTeaserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(careServiceTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(careServiceTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCareServiceTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        careServiceTeaser.setId(longCount.incrementAndGet());

        // Create the CareServiceTeaser
        CareServiceTeaserDTO careServiceTeaserDTO = careServiceTeaserMapper.toDto(careServiceTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCareServiceTeaserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(careServiceTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CareServiceTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCareServiceTeaser() throws Exception {
        // Initialize the database
        insertedCareServiceTeaser = careServiceTeaserRepository.saveAndFlush(careServiceTeaser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the careServiceTeaser
        restCareServiceTeaserMockMvc
            .perform(delete(ENTITY_API_URL_ID, careServiceTeaser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return careServiceTeaserRepository.count();
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

    protected CareServiceTeaser getPersistedCareServiceTeaser(CareServiceTeaser careServiceTeaser) {
        return careServiceTeaserRepository.findById(careServiceTeaser.getId()).orElseThrow();
    }

    protected void assertPersistedCareServiceTeaserToMatchAllProperties(CareServiceTeaser expectedCareServiceTeaser) {
        assertCareServiceTeaserAllPropertiesEquals(expectedCareServiceTeaser, getPersistedCareServiceTeaser(expectedCareServiceTeaser));
    }

    protected void assertPersistedCareServiceTeaserToMatchUpdatableProperties(CareServiceTeaser expectedCareServiceTeaser) {
        assertCareServiceTeaserAllUpdatablePropertiesEquals(
            expectedCareServiceTeaser,
            getPersistedCareServiceTeaser(expectedCareServiceTeaser)
        );
    }
}
