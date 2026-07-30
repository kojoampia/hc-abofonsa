package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.WaitlistSignupQueryService;
import net.jojoaddison.abofonsa.preview.service.WaitlistSignupService;
import net.jojoaddison.abofonsa.preview.service.criteria.WaitlistSignupCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSignupDTO;
import net.jojoaddison.abofonsa.preview.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.WaitlistSignup}.
 */
@RestController
@RequestMapping("/api/waitlist-signups")
public class WaitlistSignupResource {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistSignupResource.class);

    private static final String ENTITY_NAME = "waitlistSignup";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final WaitlistSignupService waitlistSignupService;

    private final WaitlistSignupRepository waitlistSignupRepository;

    private final WaitlistSignupQueryService waitlistSignupQueryService;

    public WaitlistSignupResource(
        WaitlistSignupService waitlistSignupService,
        WaitlistSignupRepository waitlistSignupRepository,
        WaitlistSignupQueryService waitlistSignupQueryService
    ) {
        this.waitlistSignupService = waitlistSignupService;
        this.waitlistSignupRepository = waitlistSignupRepository;
        this.waitlistSignupQueryService = waitlistSignupQueryService;
    }

    /**
     * {@code POST  /waitlist-signups} : Create a new waitlistSignup.
     *
     * @param waitlistSignupDTO the waitlistSignupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new waitlistSignupDTO, or with status {@code 400 (Bad Request)} if the waitlistSignup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WaitlistSignupDTO> createWaitlistSignup(@Valid @RequestBody WaitlistSignupDTO waitlistSignupDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save WaitlistSignup : {}", waitlistSignupDTO);
        if (waitlistSignupDTO.getId() != null) {
            throw new BadRequestAlertException("A new waitlistSignup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        waitlistSignupDTO = waitlistSignupService.save(waitlistSignupDTO);
        return ResponseEntity.created(new URI("/api/waitlist-signups/" + waitlistSignupDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, waitlistSignupDTO.getId().toString()))
            .body(waitlistSignupDTO);
    }

    /**
     * {@code PUT  /waitlist-signups/:id} : Updates an existing waitlistSignup.
     *
     * @param id the id of the waitlistSignupDTO to save.
     * @param waitlistSignupDTO the waitlistSignupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waitlistSignupDTO,
     * or with status {@code 400 (Bad Request)} if the waitlistSignupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the waitlistSignupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WaitlistSignupDTO> updateWaitlistSignup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WaitlistSignupDTO waitlistSignupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WaitlistSignup : {}, {}", id, waitlistSignupDTO);
        if (waitlistSignupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waitlistSignupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waitlistSignupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        waitlistSignupDTO = waitlistSignupService.update(waitlistSignupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, waitlistSignupDTO.getId().toString()))
            .body(waitlistSignupDTO);
    }

    /**
     * {@code PATCH  /waitlist-signups/:id} : Partial updates given fields of an existing waitlistSignup, field will ignore if it is null
     *
     * @param id the id of the waitlistSignupDTO to save.
     * @param waitlistSignupDTO the waitlistSignupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waitlistSignupDTO,
     * or with status {@code 400 (Bad Request)} if the waitlistSignupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the waitlistSignupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the waitlistSignupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WaitlistSignupDTO> partialUpdateWaitlistSignup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WaitlistSignupDTO waitlistSignupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WaitlistSignup partially : {}, {}", id, waitlistSignupDTO);
        if (waitlistSignupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waitlistSignupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waitlistSignupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WaitlistSignupDTO> result = waitlistSignupService.partialUpdate(waitlistSignupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, waitlistSignupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /waitlist-signups} : get all the Waitlist Signups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Waitlist Signups in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WaitlistSignupDTO>> getAllWaitlistSignups(
        WaitlistSignupCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WaitlistSignups by criteria: {}", criteria);

        Page<WaitlistSignupDTO> page = waitlistSignupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /waitlist-signups/count} : count all the waitlistSignups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWaitlistSignups(WaitlistSignupCriteria criteria) {
        LOG.debug("REST request to count WaitlistSignups by criteria: {}", criteria);
        return ResponseEntity.ok().body(waitlistSignupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /waitlist-signups/:id} : get the "id" waitlistSignup.
     *
     * @param id the id of the waitlistSignupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the waitlistSignupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WaitlistSignupDTO> getWaitlistSignup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WaitlistSignup : {}", id);
        Optional<WaitlistSignupDTO> waitlistSignupDTO = waitlistSignupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(waitlistSignupDTO);
    }

    /**
     * {@code DELETE  /waitlist-signups/:id} : delete the "id" waitlistSignup.
     *
     * @param id the id of the waitlistSignupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistSignup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WaitlistSignup : {}", id);
        waitlistSignupService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
