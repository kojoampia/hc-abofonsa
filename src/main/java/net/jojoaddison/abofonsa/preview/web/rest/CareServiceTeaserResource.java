package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.CareServiceTeaserRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.CareServiceTeaserService;
import net.jojoaddison.abofonsa.preview.service.dto.CareServiceTeaserDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/care-service-teasers")
@Secured(AuthoritiesConstants.ADMIN)
public class CareServiceTeaserResource {

    private static final Logger LOG = LoggerFactory.getLogger(CareServiceTeaserResource.class);

    private static final String ENTITY_NAME = "careServiceTeaser";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final CareServiceTeaserService careServiceTeaserService;

    private final CareServiceTeaserRepository careServiceTeaserRepository;

    public CareServiceTeaserResource(
        CareServiceTeaserService careServiceTeaserService,
        CareServiceTeaserRepository careServiceTeaserRepository
    ) {
        this.careServiceTeaserService = careServiceTeaserService;
        this.careServiceTeaserRepository = careServiceTeaserRepository;
    }

    /**
     * {@code POST  /care-service-teasers} : Create a new careServiceTeaser.
     *
     * @param careServiceTeaserDTO the careServiceTeaserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new careServiceTeaserDTO, or with status {@code 400 (Bad Request)} if the careServiceTeaser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CareServiceTeaserDTO> createCareServiceTeaser(@Valid @RequestBody CareServiceTeaserDTO careServiceTeaserDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CareServiceTeaser : {}", careServiceTeaserDTO);
        if (careServiceTeaserDTO.getId() != null) {
            throw new BadRequestAlertException("A new careServiceTeaser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        careServiceTeaserDTO = careServiceTeaserService.save(careServiceTeaserDTO);
        return ResponseEntity.created(new URI("/api/care-service-teasers/" + careServiceTeaserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, careServiceTeaserDTO.getId().toString()))
            .body(careServiceTeaserDTO);
    }

    /**
     * {@code PUT  /care-service-teasers/:id} : Updates an existing careServiceTeaser.
     *
     * @param id the id of the careServiceTeaserDTO to save.
     * @param careServiceTeaserDTO the careServiceTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated careServiceTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the careServiceTeaserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the careServiceTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CareServiceTeaserDTO> updateCareServiceTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CareServiceTeaserDTO careServiceTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CareServiceTeaser : {}, {}", id, careServiceTeaserDTO);
        if (careServiceTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, careServiceTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!careServiceTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        careServiceTeaserDTO = careServiceTeaserService.update(careServiceTeaserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, careServiceTeaserDTO.getId().toString()))
            .body(careServiceTeaserDTO);
    }

    /**
     * {@code PATCH  /care-service-teasers/:id} : Partial updates given fields of an existing careServiceTeaser, field will ignore if it is null
     *
     * @param id the id of the careServiceTeaserDTO to save.
     * @param careServiceTeaserDTO the careServiceTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated careServiceTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the careServiceTeaserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the careServiceTeaserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the careServiceTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CareServiceTeaserDTO> partialUpdateCareServiceTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CareServiceTeaserDTO careServiceTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CareServiceTeaser partially : {}, {}", id, careServiceTeaserDTO);
        if (careServiceTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, careServiceTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!careServiceTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CareServiceTeaserDTO> result = careServiceTeaserService.partialUpdate(careServiceTeaserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, careServiceTeaserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /care-service-teasers} : get all the Care Service Teasers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Care Service Teasers in body.
     */
    @GetMapping("")
    public List<CareServiceTeaserDTO> getAllCareServiceTeasers() {
        LOG.debug("REST request to get all CareServiceTeasers");
        return careServiceTeaserService.findAll();
    }

    /**
     * {@code GET  /care-service-teasers/:id} : get the "id" careServiceTeaser.
     *
     * @param id the id of the careServiceTeaserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the careServiceTeaserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CareServiceTeaserDTO> getCareServiceTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CareServiceTeaser : {}", id);
        Optional<CareServiceTeaserDTO> careServiceTeaserDTO = careServiceTeaserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(careServiceTeaserDTO);
    }

    /**
     * {@code DELETE  /care-service-teasers/:id} : delete the "id" careServiceTeaser.
     *
     * @param id the id of the careServiceTeaserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareServiceTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CareServiceTeaser : {}", id);
        careServiceTeaserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
