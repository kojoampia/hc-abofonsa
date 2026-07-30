package net.jojoaddison.abofonsa.preview.web.rest;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.service.DataExportLogQueryService;
import net.jojoaddison.abofonsa.preview.service.DataExportLogService;
import net.jojoaddison.abofonsa.preview.service.criteria.DataExportLogCriteria;
import net.jojoaddison.abofonsa.preview.service.dto.DataExportLogDTO;
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
 * REST controller for managing {@link net.jojoaddison.abofonsa.preview.domain.DataExportLog}.
 */
@RestController
@RequestMapping("/api/data-export-logs")
public class DataExportLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(DataExportLogResource.class);

    private final DataExportLogService dataExportLogService;

    private final DataExportLogQueryService dataExportLogQueryService;

    public DataExportLogResource(DataExportLogService dataExportLogService, DataExportLogQueryService dataExportLogQueryService) {
        this.dataExportLogService = dataExportLogService;
        this.dataExportLogQueryService = dataExportLogQueryService;
    }

    /**
     * {@code GET  /data-export-logs} : get all the Data Export Logs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Data Export Logs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DataExportLogDTO>> getAllDataExportLogs(
        DataExportLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DataExportLogs by criteria: {}", criteria);

        Page<DataExportLogDTO> page = dataExportLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /data-export-logs/count} : count all the dataExportLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDataExportLogs(DataExportLogCriteria criteria) {
        LOG.debug("REST request to count DataExportLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(dataExportLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /data-export-logs/:id} : get the "id" dataExportLog.
     *
     * @param id the id of the dataExportLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataExportLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DataExportLogDTO> getDataExportLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DataExportLog : {}", id);
        Optional<DataExportLogDTO> dataExportLogDTO = dataExportLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dataExportLogDTO);
    }
}
