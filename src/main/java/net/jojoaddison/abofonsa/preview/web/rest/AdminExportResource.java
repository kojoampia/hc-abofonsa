package net.jojoaddison.abofonsa.preview.web.rest;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.security.SecurityUtils;
import net.jojoaddison.abofonsa.preview.service.DataExportService;
import net.jojoaddison.abofonsa.preview.service.MetricQueryService;
import net.jojoaddison.abofonsa.preview.service.MetricRollupEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/** CSV exports of the captured emails and of any metric series. */
@RestController
@RequestMapping("/api/admin/export")
@Secured(AuthoritiesConstants.ADMIN)
public class AdminExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminExportResource.class);

    private final DataExportService dataExportService;
    private final MetricQueryService metricQueryService;

    public AdminExportResource(DataExportService dataExportService, MetricQueryService metricQueryService) {
        this.dataExportService = dataExportService;
        this.metricQueryService = metricQueryService;
    }

    /** {@code GET /api/admin/export/waitlist.csv} : the captured emails. */
    @GetMapping(value = "/waitlist.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportWaitlist(
        @RequestParam(value = "status", required = false) SignupStatus status,
        @RequestParam(value = "from", required = false) Instant from,
        @RequestParam(value = "to", required = false) Instant to
    ) {
        LOG.debug("REST request to export the waitlist");
        String csv = dataExportService.exportSignupsCsv(status, from, to, currentUser());
        return csvResponse(csv, "waitlist.csv");
    }

    /**
     * {@code GET /api/admin/export/metrics.csv} : any series, at any zoom, optionally faceted.
     *
     * <p>Takes the same parameters as the chart endpoint on purpose — whatever is on screen is what
     * downloads, without a second set of filters to keep in step.
     */
    @GetMapping(value = "/metrics.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportMetrics(
        @RequestParam("metric") MetricKey metric,
        @RequestParam(value = "bucket", defaultValue = "DAY") BucketType bucket,
        @RequestParam(value = "from", required = false) Instant from,
        @RequestParam(value = "to", required = false) Instant to,
        @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Instant end = to != null ? to : Instant.now().plus(1, ChronoUnit.MINUTES);
        Instant start = from != null ? from : MetricRollupEngine.floor(end.minus(30, ChronoUnit.DAYS), bucket);
        String csv = dataExportService.exportSeriesCsv(metricQueryService.series(metric, bucket, start, end, dimension), currentUser());
        return csvResponse(csv, "metrics-" + metric.name().toLowerCase() + "-" + bucket.name().toLowerCase() + ".csv");
    }

    private static ResponseEntity<byte[]> csvResponse(String csv, String filename) {
        // UTF-8 BOM: without it Excel on Windows reads the file as the system codepage and mangles
        // every non-ASCII character in a name or organisation.
        byte[] body = ("﻿" + csv).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(body);
    }

    private static String currentUser() {
        return SecurityUtils.getCurrentUserLogin().orElse("unknown");
    }
}
