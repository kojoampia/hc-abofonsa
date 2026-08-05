package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierTeaserRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.PledgeTierTeaserService;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierTeaserDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/pledge-tier-teasers")
@Secured(AuthoritiesConstants.ADMIN)
public class PledgeTierTeaserResource {

    private static final Logger LOG = LoggerFactory.getLogger(PledgeTierTeaserResource.class);

    private static final String ENTITY_NAME = "pledgeTierTeaser";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final PledgeTierTeaserService pledgeTierTeaserService;

    private final PledgeTierTeaserRepository pledgeTierTeaserRepository;

    public PledgeTierTeaserResource(
        PledgeTierTeaserService pledgeTierTeaserService,
        PledgeTierTeaserRepository pledgeTierTeaserRepository
    ) {
        this.pledgeTierTeaserService = pledgeTierTeaserService;
        this.pledgeTierTeaserRepository = pledgeTierTeaserRepository;
    }

    /**
     * {@code POST  /pledge-tier-teasers} : Create a new pledgeTierTeaser.
     *
     * @param pledgeTierTeaserDTO the pledgeTierTeaserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pledgeTierTeaserDTO, or with status {@code 400 (Bad Request)} if the pledgeTierTeaser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PledgeTierTeaserDTO> createPledgeTierTeaser(@Valid @RequestBody PledgeTierTeaserDTO pledgeTierTeaserDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PledgeTierTeaser : {}", pledgeTierTeaserDTO);
        if (pledgeTierTeaserDTO.getId() != null) {
            throw new BadRequestAlertException("A new pledgeTierTeaser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pledgeTierTeaserDTO = pledgeTierTeaserService.save(pledgeTierTeaserDTO);
        return ResponseEntity.created(new URI("/api/pledge-tier-teasers/" + pledgeTierTeaserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pledgeTierTeaserDTO.getId().toString()))
            .body(pledgeTierTeaserDTO);
    }

    /**
     * {@code PUT  /pledge-tier-teasers/:id} : Updates an existing pledgeTierTeaser.
     *
     * @param id the id of the pledgeTierTeaserDTO to save.
     * @param pledgeTierTeaserDTO the pledgeTierTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pledgeTierTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the pledgeTierTeaserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pledgeTierTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PledgeTierTeaserDTO> updatePledgeTierTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PledgeTierTeaserDTO pledgeTierTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PledgeTierTeaser : {}, {}", id, pledgeTierTeaserDTO);
        if (pledgeTierTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pledgeTierTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pledgeTierTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pledgeTierTeaserDTO = pledgeTierTeaserService.update(pledgeTierTeaserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pledgeTierTeaserDTO.getId().toString()))
            .body(pledgeTierTeaserDTO);
    }

    /**
     * {@code PATCH  /pledge-tier-teasers/:id} : Partial updates given fields of an existing pledgeTierTeaser, field will ignore if it is null
     *
     * @param id the id of the pledgeTierTeaserDTO to save.
     * @param pledgeTierTeaserDTO the pledgeTierTeaserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pledgeTierTeaserDTO,
     * or with status {@code 400 (Bad Request)} if the pledgeTierTeaserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pledgeTierTeaserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pledgeTierTeaserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PledgeTierTeaserDTO> partialUpdatePledgeTierTeaser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PledgeTierTeaserDTO pledgeTierTeaserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PledgeTierTeaser partially : {}, {}", id, pledgeTierTeaserDTO);
        if (pledgeTierTeaserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pledgeTierTeaserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pledgeTierTeaserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PledgeTierTeaserDTO> result = pledgeTierTeaserService.partialUpdate(pledgeTierTeaserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pledgeTierTeaserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pledge-tier-teasers} : get all the Pledge Tier Teasers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pledge Tier Teasers in body.
     */
    @GetMapping("")
    public List<PledgeTierTeaserDTO> getAllPledgeTierTeasers() {
        LOG.debug("REST request to get all PledgeTierTeasers");
        return pledgeTierTeaserService.findAll();
    }

    /**
     * {@code GET  /pledge-tier-teasers/:id} : get the "id" pledgeTierTeaser.
     *
     * @param id the id of the pledgeTierTeaserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pledgeTierTeaserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PledgeTierTeaserDTO> getPledgeTierTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PledgeTierTeaser : {}", id);
        Optional<PledgeTierTeaserDTO> pledgeTierTeaserDTO = pledgeTierTeaserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pledgeTierTeaserDTO);
    }

    /**
     * {@code DELETE  /pledge-tier-teasers/:id} : delete the "id" pledgeTierTeaser.
     *
     * @param id the id of the pledgeTierTeaserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePledgeTierTeaser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PledgeTierTeaser : {}", id);
        pledgeTierTeaserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
