package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.SocialLinkAsserts.*;
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
import net.jojoaddison.abofonsa.preview.domain.SocialLink;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SocialPlatform;
import net.jojoaddison.abofonsa.preview.repository.SocialLinkRepository;
import net.jojoaddison.abofonsa.preview.service.dto.SocialLinkDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.SocialLinkMapper;
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
 * Integration tests for the {@link SocialLinkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SocialLinkResourceIT {

    private static final SocialPlatform DEFAULT_PLATFORM = SocialPlatform.X;
    private static final SocialPlatform UPDATED_PLATFORM = SocialPlatform.LINKEDIN;

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ICON_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ICON_KEY = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/social-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    @Autowired
    private SocialLinkMapper socialLinkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSocialLinkMockMvc;

    private SocialLink socialLink;

    private SocialLink insertedSocialLink;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialLink createEntity() {
        return new SocialLink()
            .platform(DEFAULT_PLATFORM)
            .label(DEFAULT_LABEL)
            .url(DEFAULT_URL)
            .iconKey(DEFAULT_ICON_KEY)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialLink createUpdatedEntity() {
        return new SocialLink()
            .platform(UPDATED_PLATFORM)
            .label(UPDATED_LABEL)
            .url(UPDATED_URL)
            .iconKey(UPDATED_ICON_KEY)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        socialLink = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSocialLink != null) {
            socialLinkRepository.delete(insertedSocialLink);
            insertedSocialLink = null;
        }
    }

    @Test
    @Transactional
    void createSocialLink() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);
        var returnedSocialLinkDTO = om.readValue(
            restSocialLinkMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SocialLinkDTO.class
        );

        // Validate the SocialLink in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSocialLink = socialLinkMapper.toEntity(returnedSocialLinkDTO);
        assertSocialLinkUpdatableFieldsEquals(returnedSocialLink, getPersistedSocialLink(returnedSocialLink));

        insertedSocialLink = returnedSocialLink;
    }

    @Test
    @Transactional
    void createSocialLinkWithExistingId() throws Exception {
        // Create the SocialLink with an existing ID
        socialLink.setId(1L);
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPlatformIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialLink.setPlatform(null);

        // Create the SocialLink, which fails.
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialLink.setLabel(null);

        // Create the SocialLink, which fails.
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialLink.setUrl(null);

        // Create the SocialLink, which fails.
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialLink.setDisplayOrder(null);

        // Create the SocialLink, which fails.
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialLink.setActive(null);

        // Create the SocialLink, which fails.
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        restSocialLinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSocialLinks() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        // Get all the socialLinkList
        restSocialLinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].iconKey").value(hasItem(DEFAULT_ICON_KEY)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSocialLink() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        // Get the socialLink
        restSocialLinkMockMvc
            .perform(get(ENTITY_API_URL_ID, socialLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(socialLink.getId().intValue()))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.iconKey").value(DEFAULT_ICON_KEY))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingSocialLink() throws Exception {
        // Get the socialLink
        restSocialLinkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSocialLink() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialLink
        SocialLink updatedSocialLink = socialLinkRepository.findById(socialLink.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSocialLink are not directly saved in db
        em.detach(updatedSocialLink);
        updatedSocialLink
            .platform(UPDATED_PLATFORM)
            .label(UPDATED_LABEL)
            .url(UPDATED_URL)
            .iconKey(UPDATED_ICON_KEY)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(updatedSocialLink);

        restSocialLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, socialLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(socialLinkDTO))
            )
            .andExpect(status().isOk());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSocialLinkToMatchAllProperties(updatedSocialLink);
    }

    @Test
    @Transactional
    void putNonExistingSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, socialLinkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(socialLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(socialLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSocialLinkWithPatch() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialLink using partial update
        SocialLink partialUpdatedSocialLink = new SocialLink();
        partialUpdatedSocialLink.setId(socialLink.getId());

        partialUpdatedSocialLink.label(UPDATED_LABEL).url(UPDATED_URL).active(UPDATED_ACTIVE);

        restSocialLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSocialLink))
            )
            .andExpect(status().isOk());

        // Validate the SocialLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSocialLinkUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSocialLink, socialLink),
            getPersistedSocialLink(socialLink)
        );
    }

    @Test
    @Transactional
    void fullUpdateSocialLinkWithPatch() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialLink using partial update
        SocialLink partialUpdatedSocialLink = new SocialLink();
        partialUpdatedSocialLink.setId(socialLink.getId());

        partialUpdatedSocialLink
            .platform(UPDATED_PLATFORM)
            .label(UPDATED_LABEL)
            .url(UPDATED_URL)
            .iconKey(UPDATED_ICON_KEY)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);

        restSocialLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialLink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSocialLink))
            )
            .andExpect(status().isOk());

        // Validate the SocialLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSocialLinkUpdatableFieldsEquals(partialUpdatedSocialLink, getPersistedSocialLink(partialUpdatedSocialLink));
    }

    @Test
    @Transactional
    void patchNonExistingSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, socialLinkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(socialLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(socialLinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSocialLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialLink.setId(longCount.incrementAndGet());

        // Create the SocialLink
        SocialLinkDTO socialLinkDTO = socialLinkMapper.toDto(socialLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialLinkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(socialLinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSocialLink() throws Exception {
        // Initialize the database
        insertedSocialLink = socialLinkRepository.saveAndFlush(socialLink);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the socialLink
        restSocialLinkMockMvc
            .perform(delete(ENTITY_API_URL_ID, socialLink.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return socialLinkRepository.count();
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

    protected SocialLink getPersistedSocialLink(SocialLink socialLink) {
        return socialLinkRepository.findById(socialLink.getId()).orElseThrow();
    }

    protected void assertPersistedSocialLinkToMatchAllProperties(SocialLink expectedSocialLink) {
        assertSocialLinkAllPropertiesEquals(expectedSocialLink, getPersistedSocialLink(expectedSocialLink));
    }

    protected void assertPersistedSocialLinkToMatchUpdatableProperties(SocialLink expectedSocialLink) {
        assertSocialLinkAllUpdatablePropertiesEquals(expectedSocialLink, getPersistedSocialLink(expectedSocialLink));
    }
}
