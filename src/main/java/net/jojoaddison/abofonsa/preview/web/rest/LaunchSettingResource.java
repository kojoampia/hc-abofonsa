package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.LaunchSettingRepository;
import net.jojoaddison.abofonsa.preview.service.LaunchSettingService;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchSettingDTO;
import net.jojoaddison.abofonsa.preview.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.LaunchSetting}.
 */
@RestController
@RequestMapping("/api/launch-settings")
public class LaunchSettingResource {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchSettingResource.class);

    private static final String ENTITY_NAME = "launchSetting";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final LaunchSettingService launchSettingService;

    private final LaunchSettingRepository launchSettingRepository;

    public LaunchSettingResource(LaunchSettingService launchSettingService, LaunchSettingRepository launchSettingRepository) {
        this.launchSettingService = launchSettingService;
        this.launchSettingRepository = launchSettingRepository;
    }

    /**
     * {@code POST  /launch-settings} : Create a new launchSetting.
     *
     * @param launchSettingDTO the launchSettingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new launchSettingDTO, or with status {@code 400 (Bad Request)} if the launchSetting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LaunchSettingDTO> createLaunchSetting(@Valid @RequestBody LaunchSettingDTO launchSettingDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LaunchSetting : {}", launchSettingDTO);
        if (launchSettingDTO.getId() != null) {
            throw new BadRequestAlertException("A new launchSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        launchSettingDTO = launchSettingService.save(launchSettingDTO);
        return ResponseEntity.created(new URI("/api/launch-settings/" + launchSettingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, launchSettingDTO.getId().toString()))
            .body(launchSettingDTO);
    }

    /**
     * {@code PUT  /launch-settings/:id} : Updates an existing launchSetting.
     *
     * @param id the id of the launchSettingDTO to save.
     * @param launchSettingDTO the launchSettingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated launchSettingDTO,
     * or with status {@code 400 (Bad Request)} if the launchSettingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the launchSettingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LaunchSettingDTO> updateLaunchSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LaunchSettingDTO launchSettingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LaunchSetting : {}, {}", id, launchSettingDTO);
        if (launchSettingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, launchSettingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!launchSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        launchSettingDTO = launchSettingService.update(launchSettingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, launchSettingDTO.getId().toString()))
            .body(launchSettingDTO);
    }

    /**
     * {@code PATCH  /launch-settings/:id} : Partial updates given fields of an existing launchSetting, field will ignore if it is null
     *
     * @param id the id of the launchSettingDTO to save.
     * @param launchSettingDTO the launchSettingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated launchSettingDTO,
     * or with status {@code 400 (Bad Request)} if the launchSettingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the launchSettingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the launchSettingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LaunchSettingDTO> partialUpdateLaunchSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LaunchSettingDTO launchSettingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LaunchSetting partially : {}, {}", id, launchSettingDTO);
        if (launchSettingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, launchSettingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!launchSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LaunchSettingDTO> result = launchSettingService.partialUpdate(launchSettingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, launchSettingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /launch-settings} : get all the Launch Settings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Launch Settings in body.
     */
    @GetMapping("")
    public List<LaunchSettingDTO> getAllLaunchSettings() {
        LOG.debug("REST request to get all LaunchSettings");
        return launchSettingService.findAll();
    }

    /**
     * {@code GET  /launch-settings/:id} : get the "id" launchSetting.
     *
     * @param id the id of the launchSettingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the launchSettingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LaunchSettingDTO> getLaunchSetting(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LaunchSetting : {}", id);
        Optional<LaunchSettingDTO> launchSettingDTO = launchSettingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(launchSettingDTO);
    }

    /**
     * {@code DELETE  /launch-settings/:id} : delete the "id" launchSetting.
     *
     * @param id the id of the launchSettingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaunchSetting(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LaunchSetting : {}", id);
        launchSettingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
