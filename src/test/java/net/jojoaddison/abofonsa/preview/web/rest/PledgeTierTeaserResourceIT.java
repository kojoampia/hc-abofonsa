package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaserAsserts.*;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.createUpdateProxyForBean;
import static net.jojoaddison.abofonsa.preview.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PledgeTierCode;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.PledgeTierTeaserMapper;
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
 * Integration tests for the {@link PledgeTierTeaserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PledgeTierTeaserResourceIT {

    private static final PledgeTierCode DEFAULT_CODE = PledgeTierCode.BRONZE;
    private static final PledgeTierCode UPDATED_CODE = PledgeTierCode.SILVER;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BLURB = "AAAAAAAAAA";
    private static final String UPDATED_BLURB = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final BigDecimal DEFAULT_VOUCHER_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_VOUCHER_VALUE = new BigDecimal(1);

    private static final String DEFAULT_HANDOFF_URL = "AAAAAAAAAA";
    private static final String UPDATED_HANDOFF_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final String ENTITY_API_URL = "/api/pledge-tier-teasers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PledgeTierTeaserRepository pledgeTierTeaserRepository;

    @Autowired
    private PledgeTierTeaserMapper pledgeTierTeaserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPledgeTierTeaserMockMvc;

    private PledgeTierTeaser pledgeTierTeaser;

    private PledgeTierTeaser insertedPledgeTierTeaser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PledgeTierTeaser createEntity() {
        return new PledgeTierTeaser()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .blurb(DEFAULT_BLURB)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .voucherValue(DEFAULT_VOUCHER_VALUE)
            .handoffUrl(DEFAULT_HANDOFF_URL)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .published(DEFAULT_PUBLISHED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PledgeTierTeaser createUpdatedEntity() {
        return new PledgeTierTeaser()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .voucherValue(UPDATED_VOUCHER_VALUE)
            .handoffUrl(UPDATED_HANDOFF_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
    }

    @BeforeEach
    void initTest() {
        pledgeTierTeaser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPledgeTierTeaser != null) {
            pledgeTierTeaserRepository.delete(insertedPledgeTierTeaser);
            insertedPledgeTierTeaser = null;
        }
    }

    @Test
    @Transactional
    void createPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);
        var returnedPledgeTierTeaserDTO = om.readValue(
            restPledgeTierTeaserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PledgeTierTeaserDTO.class
        );

        // Validate the PledgeTierTeaser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPledgeTierTeaser = pledgeTierTeaserMapper.toEntity(returnedPledgeTierTeaserDTO);
        assertPledgeTierTeaserUpdatableFieldsEquals(returnedPledgeTierTeaser, getPersistedPledgeTierTeaser(returnedPledgeTierTeaser));

        insertedPledgeTierTeaser = returnedPledgeTierTeaser;
    }

    @Test
    @Transactional
    void createPledgeTierTeaserWithExistingId() throws Exception {
        // Create the PledgeTierTeaser with an existing ID
        pledgeTierTeaser.setId(1L);
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setCode(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setName(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setAmount(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setCurrency(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHandoffUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setHandoffUrl(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setDisplayOrder(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pledgeTierTeaser.setPublished(null);

        // Create the PledgeTierTeaser, which fails.
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPledgeTierTeasers() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        // Get all the pledgeTierTeaserList
        restPledgeTierTeaserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pledgeTierTeaser.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].blurb").value(hasItem(DEFAULT_BLURB)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].voucherValue").value(hasItem(sameNumber(DEFAULT_VOUCHER_VALUE))))
            .andExpect(jsonPath("$.[*].handoffUrl").value(hasItem(DEFAULT_HANDOFF_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)));
    }

    @Test
    @Transactional
    void getPledgeTierTeaser() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        // Get the pledgeTierTeaser
        restPledgeTierTeaserMockMvc
            .perform(get(ENTITY_API_URL_ID, pledgeTierTeaser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pledgeTierTeaser.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.blurb").value(DEFAULT_BLURB))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.voucherValue").value(sameNumber(DEFAULT_VOUCHER_VALUE)))
            .andExpect(jsonPath("$.handoffUrl").value(DEFAULT_HANDOFF_URL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED));
    }

    @Test
    @Transactional
    void getNonExistingPledgeTierTeaser() throws Exception {
        // Get the pledgeTierTeaser
        restPledgeTierTeaserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPledgeTierTeaser() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierTeaser
        PledgeTierTeaser updatedPledgeTierTeaser = pledgeTierTeaserRepository.findById(pledgeTierTeaser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPledgeTierTeaser are not directly saved in db
        em.detach(updatedPledgeTierTeaser);
        updatedPledgeTierTeaser
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .voucherValue(UPDATED_VOUCHER_VALUE)
            .handoffUrl(UPDATED_HANDOFF_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(updatedPledgeTierTeaser);

        restPledgeTierTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pledgeTierTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierTeaserDTO))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPledgeTierTeaserToMatchAllProperties(updatedPledgeTierTeaser);
    }

    @Test
    @Transactional
    void putNonExistingPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pledgeTierTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pledgeTierTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePledgeTierTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierTeaser using partial update
        PledgeTierTeaser partialUpdatedPledgeTierTeaser = new PledgeTierTeaser();
        partialUpdatedPledgeTierTeaser.setId(pledgeTierTeaser.getId());

        partialUpdatedPledgeTierTeaser.name(UPDATED_NAME).handoffUrl(UPDATED_HANDOFF_URL);

        restPledgeTierTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPledgeTierTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPledgeTierTeaser))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPledgeTierTeaserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPledgeTierTeaser, pledgeTierTeaser),
            getPersistedPledgeTierTeaser(pledgeTierTeaser)
        );
    }

    @Test
    @Transactional
    void fullUpdatePledgeTierTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pledgeTierTeaser using partial update
        PledgeTierTeaser partialUpdatedPledgeTierTeaser = new PledgeTierTeaser();
        partialUpdatedPledgeTierTeaser.setId(pledgeTierTeaser.getId());

        partialUpdatedPledgeTierTeaser
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .blurb(UPDATED_BLURB)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .voucherValue(UPDATED_VOUCHER_VALUE)
            .handoffUrl(UPDATED_HANDOFF_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);

        restPledgeTierTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPledgeTierTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPledgeTierTeaser))
            )
            .andExpect(status().isOk());

        // Validate the PledgeTierTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPledgeTierTeaserUpdatableFieldsEquals(
            partialUpdatedPledgeTierTeaser,
            getPersistedPledgeTierTeaser(partialUpdatedPledgeTierTeaser)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pledgeTierTeaserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pledgeTierTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pledgeTierTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPledgeTierTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pledgeTierTeaser.setId(longCount.incrementAndGet());

        // Create the PledgeTierTeaser
        PledgeTierTeaserDTO pledgeTierTeaserDTO = pledgeTierTeaserMapper.toDto(pledgeTierTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPledgeTierTeaserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pledgeTierTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PledgeTierTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePledgeTierTeaser() throws Exception {
        // Initialize the database
        insertedPledgeTierTeaser = pledgeTierTeaserRepository.saveAndFlush(pledgeTierTeaser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pledgeTierTeaser
        restPledgeTierTeaserMockMvc
            .perform(delete(ENTITY_API_URL_ID, pledgeTierTeaser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pledgeTierTeaserRepository.count();
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

    protected PledgeTierTeaser getPersistedPledgeTierTeaser(PledgeTierTeaser pledgeTierTeaser) {
        return pledgeTierTeaserRepository.findById(pledgeTierTeaser.getId()).orElseThrow();
    }

    protected void assertPersistedPledgeTierTeaserToMatchAllProperties(PledgeTierTeaser expectedPledgeTierTeaser) {
        assertPledgeTierTeaserAllPropertiesEquals(expectedPledgeTierTeaser, getPersistedPledgeTierTeaser(expectedPledgeTierTeaser));
    }

    protected void assertPersistedPledgeTierTeaserToMatchUpdatableProperties(PledgeTierTeaser expectedPledgeTierTeaser) {
        assertPledgeTierTeaserAllUpdatablePropertiesEquals(
            expectedPledgeTierTeaser,
            getPersistedPledgeTierTeaser(expectedPledgeTierTeaser)
        );
    }
}
