package net.jojoaddison.abofonsa.preview.web.rest;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.service.MetricRollupQueryService;
import net.jojoaddison.abofonsa.preview.service.MetricRollupService;
import net.jojoaddison.abofonsa.preview.service.criteria.MetricRollupCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.MetricRollupDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.MetricRollup}.
 */
@RestController
@RequestMapping("/api/metric-rollups")
public class MetricRollupResource {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRollupResource.class);

    private final MetricRollupService metricRollupService;

    private final MetricRollupQueryService metricRollupQueryService;

    public MetricRollupResource(MetricRollupService metricRollupService, MetricRollupQueryService metricRollupQueryService) {
        this.metricRollupService = metricRollupService;
        this.metricRollupQueryService = metricRollupQueryService;
    }

    /**
     * {@code GET  /metric-rollups} : get all the Metric Rollups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Metric Rollups in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MetricRollupDTO>> getAllMetricRollups(
        MetricRollupCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get MetricRollups by criteria: {}", criteria);

        Page<MetricRollupDTO> page = metricRollupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /metric-rollups/count} : count all the metricRollups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMetricRollups(MetricRollupCriteria criteria) {
        LOG.debug("REST request to count MetricRollups by criteria: {}", criteria);
        return ResponseEntity.ok().body(metricRollupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /metric-rollups/:id} : get the "id" metricRollup.
     *
     * @param id the id of the metricRollupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metricRollupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetricRollupDTO> getMetricRollup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MetricRollup : {}", id);
        Optional<MetricRollupDTO> metricRollupDTO = metricRollupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(metricRollupDTO);
    }
}
