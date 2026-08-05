package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.PlanFeatureRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.PlanFeatureService;
import net.jojoaddison.abofonsa.preview.service.dto.PlanFeatureDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.PlanFeature}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/plan-features")
@Secured(AuthoritiesConstants.ADMIN)
public class PlanFeatureResource {

    private static final Logger LOG = LoggerFactory.getLogger(PlanFeatureResource.class);

    private static final String ENTITY_NAME = "planFeature";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final PlanFeatureService planFeatureService;

    private final PlanFeatureRepository planFeatureRepository;

    public PlanFeatureResource(PlanFeatureService planFeatureService, PlanFeatureRepository planFeatureRepository) {
        this.planFeatureService = planFeatureService;
        this.planFeatureRepository = planFeatureRepository;
    }

    /**
     * {@code POST  /plan-features} : Create a new planFeature.
     *
     * @param planFeatureDTO the planFeatureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new planFeatureDTO, or with status {@code 400 (Bad Request)} if the planFeature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlanFeatureDTO> createPlanFeature(@Valid @RequestBody PlanFeatureDTO planFeatureDTO) throws URISyntaxException {
        LOG.debug("REST request to save PlanFeature : {}", planFeatureDTO);
        if (planFeatureDTO.getId() != null) {
            throw new BadRequestAlertException("A new planFeature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        planFeatureDTO = planFeatureService.save(planFeatureDTO);
        return ResponseEntity.created(new URI("/api/plan-features/" + planFeatureDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, planFeatureDTO.getId().toString()))
            .body(planFeatureDTO);
    }

    /**
     * {@code PUT  /plan-features/:id} : Updates an existing planFeature.
     *
     * @param id the id of the planFeatureDTO to save.
     * @param planFeatureDTO the planFeatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planFeatureDTO,
     * or with status {@code 400 (Bad Request)} if the planFeatureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the planFeatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlanFeatureDTO> updatePlanFeature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlanFeatureDTO planFeatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PlanFeature : {}, {}", id, planFeatureDTO);
        if (planFeatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planFeatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planFeatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        planFeatureDTO = planFeatureService.update(planFeatureDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, planFeatureDTO.getId().toString()))
            .body(planFeatureDTO);
    }

    /**
     * {@code PATCH  /plan-features/:id} : Partial updates given fields of an existing planFeature, field will ignore if it is null
     *
     * @param id the id of the planFeatureDTO to save.
     * @param planFeatureDTO the planFeatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated planFeatureDTO,
     * or with status {@code 400 (Bad Request)} if the planFeatureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the planFeatureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the planFeatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlanFeatureDTO> partialUpdatePlanFeature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlanFeatureDTO planFeatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PlanFeature partially : {}, {}", id, planFeatureDTO);
        if (planFeatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, planFeatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!planFeatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlanFeatureDTO> result = planFeatureService.partialUpdate(planFeatureDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, planFeatureDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /plan-features} : get all the Plan Features.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Plan Features in body.
     */
    @GetMapping("")
    public List<PlanFeatureDTO> getAllPlanFeatures(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PlanFeatures");
        return planFeatureService.findAll();
    }

    /**
     * {@code GET  /plan-features/:id} : get the "id" planFeature.
     *
     * @param id the id of the planFeatureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the planFeatureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanFeatureDTO> getPlanFeature(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PlanFeature : {}", id);
        Optional<PlanFeatureDTO> planFeatureDTO = planFeatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(planFeatureDTO);
    }

    /**
     * {@code DELETE  /plan-features/:id} : delete the "id" planFeature.
     *
     * @param id the id of the planFeatureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanFeature(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PlanFeature : {}", id);
        planFeatureService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
