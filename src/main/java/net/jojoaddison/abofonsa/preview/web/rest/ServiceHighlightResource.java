package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.ServiceHighlightRepository;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.ServiceHighlightService;
import net.jojoaddison.abofonsa.preview.service.dto.ServiceHighlightDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.ServiceHighlight}.
 *
 * <p>Administrative CRUD. The class-level {@code @Secured} repeats the URL rule in
 * {@code SecurityConfiguration} on purpose: re-running the entity generator rewrites this file but
 * not that one, and a resource that quietly falls back to "any authenticated principal" is how the
 * whole waitlist table became readable to a seeded demo account.
 */
@RestController
@RequestMapping("/api/service-highlights")
@Secured(AuthoritiesConstants.ADMIN)
public class ServiceHighlightResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHighlightResource.class);

    private static final String ENTITY_NAME = "serviceHighlight";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final ServiceHighlightService serviceHighlightService;

    private final ServiceHighlightRepository serviceHighlightRepository;

    public ServiceHighlightResource(
        ServiceHighlightService serviceHighlightService,
        ServiceHighlightRepository serviceHighlightRepository
    ) {
        this.serviceHighlightService = serviceHighlightService;
        this.serviceHighlightRepository = serviceHighlightRepository;
    }

    /**
     * {@code POST  /service-highlights} : Create a new serviceHighlight.
     *
     * @param serviceHighlightDTO the serviceHighlightDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceHighlightDTO, or with status {@code 400 (Bad Request)} if the serviceHighlight has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceHighlightDTO> createServiceHighlight(@Valid @RequestBody ServiceHighlightDTO serviceHighlightDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceHighlight : {}", serviceHighlightDTO);
        if (serviceHighlightDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceHighlight cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceHighlightDTO = serviceHighlightService.save(serviceHighlightDTO);
        return ResponseEntity.created(new URI("/api/service-highlights/" + serviceHighlightDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceHighlightDTO.getId().toString()))
            .body(serviceHighlightDTO);
    }

    /**
     * {@code PUT  /service-highlights/:id} : Updates an existing serviceHighlight.
     *
     * @param id the id of the serviceHighlightDTO to save.
     * @param serviceHighlightDTO the serviceHighlightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceHighlightDTO,
     * or with status {@code 400 (Bad Request)} if the serviceHighlightDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceHighlightDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceHighlightDTO> updateServiceHighlight(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceHighlightDTO serviceHighlightDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceHighlight : {}, {}", id, serviceHighlightDTO);
        if (serviceHighlightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceHighlightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceHighlightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceHighlightDTO = serviceHighlightService.update(serviceHighlightDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceHighlightDTO.getId().toString()))
            .body(serviceHighlightDTO);
    }

    /**
     * {@code PATCH  /service-highlights/:id} : Partial updates given fields of an existing serviceHighlight, field will ignore if it is null
     *
     * @param id the id of the serviceHighlightDTO to save.
     * @param serviceHighlightDTO the serviceHighlightDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceHighlightDTO,
     * or with status {@code 400 (Bad Request)} if the serviceHighlightDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceHighlightDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceHighlightDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceHighlightDTO> partialUpdateServiceHighlight(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceHighlightDTO serviceHighlightDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ServiceHighlight partially : {}, {}", id, serviceHighlightDTO);
        if (serviceHighlightDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceHighlightDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceHighlightRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceHighlightDTO> result = serviceHighlightService.partialUpdate(serviceHighlightDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceHighlightDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /service-highlights} : get all the Service Highlights.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Service Highlights in body.
     */
    @GetMapping("")
    public List<ServiceHighlightDTO> getAllServiceHighlights(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ServiceHighlights");
        return serviceHighlightService.findAll();
    }

    /**
     * {@code GET  /service-highlights/:id} : get the "id" serviceHighlight.
     *
     * @param id the id of the serviceHighlightDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceHighlightDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceHighlightDTO> getServiceHighlight(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceHighlight : {}", id);
        Optional<ServiceHighlightDTO> serviceHighlightDTO = serviceHighlightService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceHighlightDTO);
    }

    /**
     * {@code DELETE  /service-highlights/:id} : delete the "id" serviceHighlight.
     *
     * @param id the id of the serviceHighlightDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceHighlight(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceHighlight : {}", id);
        serviceHighlightService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
