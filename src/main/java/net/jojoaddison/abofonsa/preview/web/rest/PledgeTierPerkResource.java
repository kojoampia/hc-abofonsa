package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.PledgeTierPerkRepository;
import net.jojoaddison.abofonsa.preview.service.PledgeTierPerkService;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierPerkDTO;
import net.jojoaddison.abofonsa.preview.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk}.
 */
@RestController
@RequestMapping("/api/pledge-tier-perks")
public class PledgeTierPerkResource {

    private static final Logger LOG = LoggerFactory.getLogger(PledgeTierPerkResource.class);

    private static final String ENTITY_NAME = "pledgeTierPerk";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final PledgeTierPerkService pledgeTierPerkService;

    private final PledgeTierPerkRepository pledgeTierPerkRepository;

    public PledgeTierPerkResource(PledgeTierPerkService pledgeTierPerkService, PledgeTierPerkRepository pledgeTierPerkRepository) {
        this.pledgeTierPerkService = pledgeTierPerkService;
        this.pledgeTierPerkRepository = pledgeTierPerkRepository;
    }

    /**
     * {@code POST  /pledge-tier-perks} : Create a new pledgeTierPerk.
     *
     * @param pledgeTierPerkDTO the pledgeTierPerkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pledgeTierPerkDTO, or with status {@code 400 (Bad Request)} if the pledgeTierPerk has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PledgeTierPerkDTO> createPledgeTierPerk(@Valid @RequestBody PledgeTierPerkDTO pledgeTierPerkDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PledgeTierPerk : {}", pledgeTierPerkDTO);
        if (pledgeTierPerkDTO.getId() != null) {
            throw new BadRequestAlertException("A new pledgeTierPerk cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pledgeTierPerkDTO = pledgeTierPerkService.save(pledgeTierPerkDTO);
        return ResponseEntity.created(new URI("/api/pledge-tier-perks/" + pledgeTierPerkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pledgeTierPerkDTO.getId().toString()))
            .body(pledgeTierPerkDTO);
    }

    /**
     * {@code PUT  /pledge-tier-perks/:id} : Updates an existing pledgeTierPerk.
     *
     * @param id the id of the pledgeTierPerkDTO to save.
     * @param pledgeTierPerkDTO the pledgeTierPerkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pledgeTierPerkDTO,
     * or with status {@code 400 (Bad Request)} if the pledgeTierPerkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pledgeTierPerkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PledgeTierPerkDTO> updatePledgeTierPerk(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PledgeTierPerkDTO pledgeTierPerkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PledgeTierPerk : {}, {}", id, pledgeTierPerkDTO);
        if (pledgeTierPerkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pledgeTierPerkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pledgeTierPerkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pledgeTierPerkDTO = pledgeTierPerkService.update(pledgeTierPerkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pledgeTierPerkDTO.getId().toString()))
            .body(pledgeTierPerkDTO);
    }

    /**
     * {@code PATCH  /pledge-tier-perks/:id} : Partial updates given fields of an existing pledgeTierPerk, field will ignore if it is null
     *
     * @param id the id of the pledgeTierPerkDTO to save.
     * @param pledgeTierPerkDTO the pledgeTierPerkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pledgeTierPerkDTO,
     * or with status {@code 400 (Bad Request)} if the pledgeTierPerkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pledgeTierPerkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pledgeTierPerkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PledgeTierPerkDTO> partialUpdatePledgeTierPerk(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PledgeTierPerkDTO pledgeTierPerkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PledgeTierPerk partially : {}, {}", id, pledgeTierPerkDTO);
        if (pledgeTierPerkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pledgeTierPerkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pledgeTierPerkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PledgeTierPerkDTO> result = pledgeTierPerkService.partialUpdate(pledgeTierPerkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pledgeTierPerkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pledge-tier-perks} : get all the Pledge Tier Perks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pledge Tier Perks in body.
     */
    @GetMapping("")
    public List<PledgeTierPerkDTO> getAllPledgeTierPerks(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PledgeTierPerks");
        return pledgeTierPerkService.findAll();
    }

    /**
     * {@code GET  /pledge-tier-perks/:id} : get the "id" pledgeTierPerk.
     *
     * @param id the id of the pledgeTierPerkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pledgeTierPerkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PledgeTierPerkDTO> getPledgeTierPerk(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PledgeTierPerk : {}", id);
        Optional<PledgeTierPerkDTO> pledgeTierPerkDTO = pledgeTierPerkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pledgeTierPerkDTO);
    }

    /**
     * {@code DELETE  /pledge-tier-perks/:id} : delete the "id" pledgeTierPerk.
     *
     * @param id the id of the pledgeTierPerkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePledgeTierPerk(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PledgeTierPerk : {}", id);
        pledgeTierPerkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
