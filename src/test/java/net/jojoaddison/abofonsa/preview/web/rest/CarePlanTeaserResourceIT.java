package net.jojoaddison.abofonsa.preview.web.rest;

import static net.jojoaddison.abofonsa.preview.domain.CarePlanTeaserAsserts.*;
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
import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import net.jojoaddison.abofonsa.preview.repository.CarePlanTeaserRepository;
import net.jojoaddison.abofonsa.preview.service.dto.CarePlanTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.mapper.CarePlanTeaserMapper;
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
 * Integration tests for the {@link CarePlanTeaserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarePlanTeaserResourceIT {

    private static final PlanCode DEFAULT_CODE = PlanCode.PEAR;
    private static final PlanCode UPDATED_CODE = PlanCode.PAWPAW;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FOR_WHO = "AAAAAAAAAA";
    private static final String UPDATED_FOR_WHO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_AMOUNT = new BigDecimal(1);

    private static final String DEFAULT_PRICE_CURRENCY = "AAA";
    private static final String UPDATED_PRICE_CURRENCY = "BBB";

    private static final String DEFAULT_PRICE_PERIOD = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_PERIOD = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE_NOTE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_FEATURED = false;
    private static final Boolean UPDATED_FEATURED = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final String ENTITY_API_URL = "/api/care-plan-teasers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarePlanTeaserRepository carePlanTeaserRepository;

    @Autowired
    private CarePlanTeaserMapper carePlanTeaserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarePlanTeaserMockMvc;

    private CarePlanTeaser carePlanTeaser;

    private CarePlanTeaser insertedCarePlanTeaser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarePlanTeaser createEntity() {
        return new CarePlanTeaser()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .forWho(DEFAULT_FOR_WHO)
            .priceAmount(DEFAULT_PRICE_AMOUNT)
            .priceCurrency(DEFAULT_PRICE_CURRENCY)
            .pricePeriod(DEFAULT_PRICE_PERIOD)
            .priceNote(DEFAULT_PRICE_NOTE)
            .featured(DEFAULT_FEATURED)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .published(DEFAULT_PUBLISHED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarePlanTeaser createUpdatedEntity() {
        return new CarePlanTeaser()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .forWho(UPDATED_FOR_WHO)
            .priceAmount(UPDATED_PRICE_AMOUNT)
            .priceCurrency(UPDATED_PRICE_CURRENCY)
            .pricePeriod(UPDATED_PRICE_PERIOD)
            .priceNote(UPDATED_PRICE_NOTE)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
    }

    @BeforeEach
    void initTest() {
        carePlanTeaser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCarePlanTeaser != null) {
            carePlanTeaserRepository.delete(insertedCarePlanTeaser);
            insertedCarePlanTeaser = null;
        }
    }

    @Test
    @Transactional
    void createCarePlanTeaser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);
        var returnedCarePlanTeaserDTO = om.readValue(
            restCarePlanTeaserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarePlanTeaserDTO.class
        );

        // Validate the CarePlanTeaser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarePlanTeaser = carePlanTeaserMapper.toEntity(returnedCarePlanTeaserDTO);
        assertCarePlanTeaserUpdatableFieldsEquals(returnedCarePlanTeaser, getPersistedCarePlanTeaser(returnedCarePlanTeaser));

        insertedCarePlanTeaser = returnedCarePlanTeaser;
    }

    @Test
    @Transactional
    void createCarePlanTeaserWithExistingId() throws Exception {
        // Create the CarePlanTeaser with an existing ID
        carePlanTeaser.setId(1L);
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setCode(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setName(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setPriceAmount(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setPriceCurrency(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPricePeriodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setPricePeriod(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFeaturedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setFeatured(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setDisplayOrder(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carePlanTeaser.setPublished(null);

        // Create the CarePlanTeaser, which fails.
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCarePlanTeasers() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        // Get all the carePlanTeaserList
        restCarePlanTeaserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carePlanTeaser.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].forWho").value(hasItem(DEFAULT_FOR_WHO)))
            .andExpect(jsonPath("$.[*].priceAmount").value(hasItem(sameNumber(DEFAULT_PRICE_AMOUNT))))
            .andExpect(jsonPath("$.[*].priceCurrency").value(hasItem(DEFAULT_PRICE_CURRENCY)))
            .andExpect(jsonPath("$.[*].pricePeriod").value(hasItem(DEFAULT_PRICE_PERIOD)))
            .andExpect(jsonPath("$.[*].priceNote").value(hasItem(DEFAULT_PRICE_NOTE)))
            .andExpect(jsonPath("$.[*].featured").value(hasItem(DEFAULT_FEATURED)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)));
    }

    @Test
    @Transactional
    void getCarePlanTeaser() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        // Get the carePlanTeaser
        restCarePlanTeaserMockMvc
            .perform(get(ENTITY_API_URL_ID, carePlanTeaser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carePlanTeaser.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.forWho").value(DEFAULT_FOR_WHO))
            .andExpect(jsonPath("$.priceAmount").value(sameNumber(DEFAULT_PRICE_AMOUNT)))
            .andExpect(jsonPath("$.priceCurrency").value(DEFAULT_PRICE_CURRENCY))
            .andExpect(jsonPath("$.pricePeriod").value(DEFAULT_PRICE_PERIOD))
            .andExpect(jsonPath("$.priceNote").value(DEFAULT_PRICE_NOTE))
            .andExpect(jsonPath("$.featured").value(DEFAULT_FEATURED))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED));
    }

    @Test
    @Transactional
    void getNonExistingCarePlanTeaser() throws Exception {
        // Get the carePlanTeaser
        restCarePlanTeaserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarePlanTeaser() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carePlanTeaser
        CarePlanTeaser updatedCarePlanTeaser = carePlanTeaserRepository.findById(carePlanTeaser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarePlanTeaser are not directly saved in db
        em.detach(updatedCarePlanTeaser);
        updatedCarePlanTeaser
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .forWho(UPDATED_FOR_WHO)
            .priceAmount(UPDATED_PRICE_AMOUNT)
            .priceCurrency(UPDATED_PRICE_CURRENCY)
            .pricePeriod(UPDATED_PRICE_PERIOD)
            .priceNote(UPDATED_PRICE_NOTE)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(updatedCarePlanTeaser);

        restCarePlanTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carePlanTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carePlanTeaserDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarePlanTeaserToMatchAllProperties(updatedCarePlanTeaser);
    }

    @Test
    @Transactional
    void putNonExistingCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carePlanTeaserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carePlanTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carePlanTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarePlanTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carePlanTeaser using partial update
        CarePlanTeaser partialUpdatedCarePlanTeaser = new CarePlanTeaser();
        partialUpdatedCarePlanTeaser.setId(carePlanTeaser.getId());

        partialUpdatedCarePlanTeaser
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .priceAmount(UPDATED_PRICE_AMOUNT)
            .priceCurrency(UPDATED_PRICE_CURRENCY)
            .pricePeriod(UPDATED_PRICE_PERIOD)
            .priceNote(UPDATED_PRICE_NOTE)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restCarePlanTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarePlanTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarePlanTeaser))
            )
            .andExpect(status().isOk());

        // Validate the CarePlanTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarePlanTeaserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarePlanTeaser, carePlanTeaser),
            getPersistedCarePlanTeaser(carePlanTeaser)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarePlanTeaserWithPatch() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carePlanTeaser using partial update
        CarePlanTeaser partialUpdatedCarePlanTeaser = new CarePlanTeaser();
        partialUpdatedCarePlanTeaser.setId(carePlanTeaser.getId());

        partialUpdatedCarePlanTeaser
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .forWho(UPDATED_FOR_WHO)
            .priceAmount(UPDATED_PRICE_AMOUNT)
            .priceCurrency(UPDATED_PRICE_CURRENCY)
            .pricePeriod(UPDATED_PRICE_PERIOD)
            .priceNote(UPDATED_PRICE_NOTE)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .published(UPDATED_PUBLISHED);

        restCarePlanTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarePlanTeaser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarePlanTeaser))
            )
            .andExpect(status().isOk());

        // Validate the CarePlanTeaser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarePlanTeaserUpdatableFieldsEquals(partialUpdatedCarePlanTeaser, getPersistedCarePlanTeaser(partialUpdatedCarePlanTeaser));
    }

    @Test
    @Transactional
    void patchNonExistingCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carePlanTeaserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carePlanTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carePlanTeaserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarePlanTeaser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carePlanTeaser.setId(longCount.incrementAndGet());

        // Create the CarePlanTeaser
        CarePlanTeaserDTO carePlanTeaserDTO = carePlanTeaserMapper.toDto(carePlanTeaser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarePlanTeaserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carePlanTeaserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarePlanTeaser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCarePlanTeaser() throws Exception {
        // Initialize the database
        insertedCarePlanTeaser = carePlanTeaserRepository.saveAndFlush(carePlanTeaser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the carePlanTeaser
        restCarePlanTeaserMockMvc
            .perform(delete(ENTITY_API_URL_ID, carePlanTeaser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return carePlanTeaserRepository.count();
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

    protected CarePlanTeaser getPersistedCarePlanTeaser(CarePlanTeaser carePlanTeaser) {
        return carePlanTeaserRepository.findById(carePlanTeaser.getId()).orElseThrow();
    }

    protected void assertPersistedCarePlanTeaserToMatchAllProperties(CarePlanTeaser expectedCarePlanTeaser) {
        assertCarePlanTeaserAllPropertiesEquals(expectedCarePlanTeaser, getPersistedCarePlanTeaser(expectedCarePlanTeaser));
    }

    protected void assertPersistedCarePlanTeaserToMatchUpdatableProperties(CarePlanTeaser expectedCarePlanTeaser) {
        assertCarePlanTeaserAllUpdatablePropertiesEquals(expectedCarePlanTeaser, getPersistedCarePlanTeaser(expectedCarePlanTeaser));
    }
}
