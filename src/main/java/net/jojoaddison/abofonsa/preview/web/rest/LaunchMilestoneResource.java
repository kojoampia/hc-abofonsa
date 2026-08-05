package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.LaunchMilestoneRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.LaunchMilestoneService;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchMilestoneDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.LaunchMilestone}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/launch-milestones")
@Secured(AuthoritiesConstants.ADMIN)
public class LaunchMilestoneResource {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchMilestoneResource.class);

    private static final String ENTITY_NAME = "launchMilestone";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final LaunchMilestoneService launchMilestoneService;

    private final LaunchMilestoneRepository launchMilestoneRepository;

    public LaunchMilestoneResource(LaunchMilestoneService launchMilestoneService, LaunchMilestoneRepository launchMilestoneRepository) {
        this.launchMilestoneService = launchMilestoneService;
        this.launchMilestoneRepository = launchMilestoneRepository;
    }

    /**
     * {@code POST  /launch-milestones} : Create a new launchMilestone.
     *
     * @param launchMilestoneDTO the launchMilestoneDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new launchMilestoneDTO, or with status {@code 400 (Bad Request)} if the launchMilestone has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LaunchMilestoneDTO> createLaunchMilestone(@Valid @RequestBody LaunchMilestoneDTO launchMilestoneDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LaunchMilestone : {}", launchMilestoneDTO);
        if (launchMilestoneDTO.getId() != null) {
            throw new BadRequestAlertException("A new launchMilestone cannot already have an ID", ENTITY_NAME, "idexists");
        }
        launchMilestoneDTO = launchMilestoneService.save(launchMilestoneDTO);
        return ResponseEntity.created(new URI("/api/launch-milestones/" + launchMilestoneDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, launchMilestoneDTO.getId().toString()))
            .body(launchMilestoneDTO);
    }

    /**
     * {@code PUT  /launch-milestones/:id} : Updates an existing launchMilestone.
     *
     * @param id the id of the launchMilestoneDTO to save.
     * @param launchMilestoneDTO the launchMilestoneDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated launchMilestoneDTO,
     * or with status {@code 400 (Bad Request)} if the launchMilestoneDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the launchMilestoneDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LaunchMilestoneDTO> updateLaunchMilestone(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LaunchMilestoneDTO launchMilestoneDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LaunchMilestone : {}, {}", id, launchMilestoneDTO);
        if (launchMilestoneDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, launchMilestoneDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!launchMilestoneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        launchMilestoneDTO = launchMilestoneService.update(launchMilestoneDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, launchMilestoneDTO.getId().toString()))
            .body(launchMilestoneDTO);
    }

    /**
     * {@code PATCH  /launch-milestones/:id} : Partial updates given fields of an existing launchMilestone, field will ignore if it is null
     *
     * @param id the id of the launchMilestoneDTO to save.
     * @param launchMilestoneDTO the launchMilestoneDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated launchMilestoneDTO,
     * or with status {@code 400 (Bad Request)} if the launchMilestoneDTO is not valid,
     * or with status {@code 404 (Not Found)} if the launchMilestoneDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the launchMilestoneDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LaunchMilestoneDTO> partialUpdateLaunchMilestone(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LaunchMilestoneDTO launchMilestoneDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LaunchMilestone partially : {}, {}", id, launchMilestoneDTO);
        if (launchMilestoneDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, launchMilestoneDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!launchMilestoneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LaunchMilestoneDTO> result = launchMilestoneService.partialUpdate(launchMilestoneDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, launchMilestoneDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /launch-milestones} : get all the Launch Milestones.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Launch Milestones in body.
     */
    @GetMapping("")
    public List<LaunchMilestoneDTO> getAllLaunchMilestones() {
        LOG.debug("REST request to get all LaunchMilestones");
        return launchMilestoneService.findAll();
    }

    /**
     * {@code GET  /launch-milestones/:id} : get the "id" launchMilestone.
     *
     * @param id the id of the launchMilestoneDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the launchMilestoneDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LaunchMilestoneDTO> getLaunchMilestone(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LaunchMilestone : {}", id);
        Optional<LaunchMilestoneDTO> launchMilestoneDTO = launchMilestoneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(launchMilestoneDTO);
    }

    /**
     * {@code DELETE  /launch-milestones/:id} : delete the "id" launchMilestone.
     *
     * @param id the id of the launchMilestoneDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaunchMilestone(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LaunchMilestone : {}", id);
        launchMilestoneService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
