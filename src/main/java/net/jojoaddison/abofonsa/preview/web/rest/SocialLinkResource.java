package net.jojoaddison.abofonsa.preview.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.repository.SocialLinkRepository;
import net.jojoaddison.abofonsa.preview.service.SocialLinkService;
import net.jojoaddison.abofonsa.preview.service.dto.SocialLinkDTO;
import net.jojoaddison.abofonsa.preview.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.SocialLink}.
 */
@RestController
@RequestMapping("/api/social-links")
public class SocialLinkResource {

    private static final Logger LOG = LoggerFactory.getLogger(SocialLinkResource.class);

    private static final String ENTITY_NAME = "socialLink";

    @Value("${jhipster.clientApp.name:abofonsaPreview}")
    private String applicationName;

    private final SocialLinkService socialLinkService;

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinkResource(SocialLinkService socialLinkService, SocialLinkRepository socialLinkRepository) {
        this.socialLinkService = socialLinkService;
        this.socialLinkRepository = socialLinkRepository;
    }

    /**
     * {@code POST  /social-links} : Create a new socialLink.
     *
     * @param socialLinkDTO the socialLinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new socialLinkDTO, or with status {@code 400 (Bad Request)} if the socialLink has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SocialLinkDTO> createSocialLink(@Valid @RequestBody SocialLinkDTO socialLinkDTO) throws URISyntaxException {
        LOG.debug("REST request to save SocialLink : {}", socialLinkDTO);
        if (socialLinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new socialLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        socialLinkDTO = socialLinkService.save(socialLinkDTO);
        return ResponseEntity.created(new URI("/api/social-links/" + socialLinkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, socialLinkDTO.getId().toString()))
            .body(socialLinkDTO);
    }

    /**
     * {@code PUT  /social-links/:id} : Updates an existing socialLink.
     *
     * @param id the id of the socialLinkDTO to save.
     * @param socialLinkDTO the socialLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialLinkDTO,
     * or with status {@code 400 (Bad Request)} if the socialLinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the socialLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SocialLinkDTO> updateSocialLink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SocialLinkDTO socialLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SocialLink : {}, {}", id, socialLinkDTO);
        if (socialLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        socialLinkDTO = socialLinkService.update(socialLinkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, socialLinkDTO.getId().toString()))
            .body(socialLinkDTO);
    }

    /**
     * {@code PATCH  /social-links/:id} : Partial updates given fields of an existing socialLink, field will ignore if it is null
     *
     * @param id the id of the socialLinkDTO to save.
     * @param socialLinkDTO the socialLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialLinkDTO,
     * or with status {@code 400 (Bad Request)} if the socialLinkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the socialLinkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the socialLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SocialLinkDTO> partialUpdateSocialLink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SocialLinkDTO socialLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SocialLink partially : {}, {}", id, socialLinkDTO);
        if (socialLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SocialLinkDTO> result = socialLinkService.partialUpdate(socialLinkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, socialLinkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /social-links} : get all the Social Links.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Social Links in body.
     */
    @GetMapping("")
    public List<SocialLinkDTO> getAllSocialLinks() {
        LOG.debug("REST request to get all SocialLinks");
        return socialLinkService.findAll();
    }

    /**
     * {@code GET  /social-links/:id} : get the "id" socialLink.
     *
     * @param id the id of the socialLinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the socialLinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SocialLinkDTO> getSocialLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SocialLink : {}", id);
        Optional<SocialLinkDTO> socialLinkDTO = socialLinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(socialLinkDTO);
    }

    /**
     * {@code DELETE  /social-links/:id} : delete the "id" socialLink.
     *
     * @param id the id of the socialLinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SocialLink : {}", id);
        socialLinkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
