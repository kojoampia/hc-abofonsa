package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.CarePlanTeaserRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.CarePlanTeaserService;
import net.jojoaddison.abofonsa.preview.service.dto.CarePlanTeaserDTO;
import net.jojoaddison.abofonsa.preview.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/care-plan-teasers")
@Secured(AuthoritiesConstants.ADMIN)
public class CarePlanTeaserResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarePlanTeaserResource.class);

    private static final String ENTITY_NAME = "carePlanTeaser";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final CarePlanTeaserService carePlanTeaserService;

    private final CarePlanTeaserRepository carePlanTeaserRepository;

    public CarePlanTeaserResource(CarePlanTeaserService carePlanTeaserService, CarePlanTeaserRepository carePlanTeaserRepository) {
        this.carePlanTeaserService = carePlanTeaserService;
        this.carePlanTeaserRepository = carePlanTeaserRepository;
    }

    /**
     * {@code POST  /care-plan-teasers} : Create a new carePlanTeaser.
     *
     * @param carePlanTeaserDTO the carePlanTeaserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carePlanTeaserDTO, or with status {@code 400 (Bad Request)} if the carePlanTeaser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarePlanTeaserDTO> createCarePlanTeaser(@Valid @RequestBody CarePlanTeaserDTO carePlanTeaserDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CarePlanTeaser : {}", carePlanTeaserDTO);
        if (carePlanTeaserDTO.getId() != null) {
            throw new BadRequestAlertException("A new carePlanTeaser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carePlanTeaserDTO = carePlanTeaserService.save(carePlanTeaserDTO);
        return ResponseEntity.created(new URI("/api/care-plan-teasers/" + carePlanTeaserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carePlanTeaserDTO.getId().toString()))
            .body(carePlanTeaserDTO);
    }

    /**
     * {@code PUT  /care-plan-teasers/:id} : Updates an existing carePlanTeaser.
     *
     * @param id the id of the carePlanTeaserDTO to save.
     * @param carePlanTeaserDTO the carePlanTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carePlanTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the carePlanTeaserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carePlanTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarePlanTeaserDTO> updateCarePlanTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CarePlanTeaserDTO carePlanTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarePlanTeaser : {}, {}", id, carePlanTeaserDTO);
        if (carePlanTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carePlanTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carePlanTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carePlanTeaserDTO = carePlanTeaserService.update(carePlanTeaserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carePlanTeaserDTO.getId().toString()))
            .body(carePlanTeaserDTO);
    }

    /**
     * {@code PATCH  /care-plan-teasers/:id} : Partial updates given fields of an existing carePlanTeaser, field will ignore if it is null
     *
     * @param id the id of the carePlanTeaserDTO to save.
     * @param carePlanTeaserDTO the carePlanTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carePlanTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the carePlanTeaserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carePlanTeaserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carePlanTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarePlanTeaserDTO> partialUpdateCarePlanTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CarePlanTeaserDTO carePlanTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarePlanTeaser partially : {}, {}", id, carePlanTeaserDTO);
        if (carePlanTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carePlanTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carePlanTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarePlanTeaserDTO> result = carePlanTeaserService.partialUpdate(carePlanTeaserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carePlanTeaserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /care-plan-teasers} : get all the Care Plan Teasers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Care Plan Teasers in body.
     */
    @GetMapping("")
    public List<CarePlanTeaserDTO> getAllCarePlanTeasers() {
        LOG.debug("REST request to get all CarePlanTeasers");
        return carePlanTeaserService.findAll();
    }

    /**
     * {@code GET  /care-plan-teasers/:id} : get the "id" carePlanTeaser.
     *
     * @param id the id of the carePlanTeaserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carePlanTeaserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarePlanTeaserDTO> getCarePlanTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarePlanTeaser : {}", id);
        Optional<CarePlanTeaserDTO> carePlanTeaserDTO = carePlanTeaserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carePlanTeaserDTO);
    }

    /**
     * {@code DELETE  /care-plan-teasers/:id} : delete the "id" carePlanTeaser.
     *
     * @param id the id of the carePlanTeaserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarePlanTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarePlanTeaser : {}", id);
        carePlanTeaserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
