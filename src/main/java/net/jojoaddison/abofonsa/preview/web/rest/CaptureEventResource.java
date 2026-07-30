package net.jojoaddison.abofonsa.preview.web.rest;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.service.CaptureEventQueryService;
import net.jojoaddison.abofonsa.preview.service.CaptureEventService;
import net.jojoaddison.abofonsa.preview.service.criteria.CaptureEventCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.CaptureEvent}.
 */
@RestController
@RequestMapping("/api/capture-events")
public class CaptureEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureEventResource.class);

    private final CaptureEventService captureEventService;

    private final CaptureEventQueryService captureEventQueryService;

    public CaptureEventResource(CaptureEventService captureEventService, CaptureEventQueryService captureEventQueryService) {
        this.captureEventService = captureEventService;
        this.captureEventQueryService = captureEventQueryService;
    }

    /**
     * {@code GET  /capture-events} : get all the Capture Events.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Capture Events in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CaptureEventDTO>> getAllCaptureEvents(
        CaptureEventCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CaptureEvents by criteria: {}", criteria);

        Page<CaptureEventDTO> page = captureEventQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /capture-events/count} : count all the captureEvents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCaptureEvents(CaptureEventCriteria criteria) {
        LOG.debug("REST request to count CaptureEvents by criteria: {}", criteria);
        return ResponseEntity.ok().body(captureEventQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /capture-events/:id} : get the "id" captureEvent.
     *
     * @param id the id of the captureEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the captureEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaptureEventDTO> getCaptureEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CaptureEvent : {}", id);
        Optional<CaptureEventDTO> captureEventDTO = captureEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(captureEventDTO);
    }
}
