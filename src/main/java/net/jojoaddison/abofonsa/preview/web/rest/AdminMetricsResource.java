package net.jojoaddison.abofonsa.preview.web.rest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.security.AuthoritiesConstants;
import net.jojoaddison.abofonsa.preview.service.MetricQueryService;
import net.jojoaddison.abofonsa.preview.service.MetricRollupEngine;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSeriesDTO;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * The dashboard's metrics API. Everything under {@code /api/admin/**} already requires
 * {@code ROLE_ADMIN} in {@code SecurityConfiguration}; the annotation restates it so the rule
 * survives someone reorganising the matchers.
 */
@RestController
@RequestMapping("/api/admin/metrics")
@Secured(AuthoritiesConstants.ADMIN)
public class AdminMetricsResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMetricsResource.class);

    private final MetricQueryService metricQueryService;
    private final MetricRollupEngine metricRollupEngine;

    public AdminMetricsResource(MetricQueryService metricQueryService, MetricRollupEngine metricRollupEngine) {
        this.metricQueryService = metricQueryService;
        this.metricRollupEngine = metricRollupEngine;
    }

    /**
     * {@code GET /api/admin/metrics/series} : the drill-down and zoom endpoint.
     *
     * <p>{@code bucket} is the zoom level (HOUR…YEAR) and {@code dimension} the optional facet to
     * split by. Omitting {@code from}/{@code to} gives a window sized to the bucket, so switching
     * zoom does not also require choosing new dates — asking for HOUR and getting five years of
     * hourly points would be neither useful nor fast.
     */
    @GetMapping("/series")
    public ResponseEntity<MetricSeriesDTO> series(
        @RequestParam("metric") MetricKey metric,
        @RequestParam(value = "bucket", defaultValue = "DAY") BucketType bucket,
        @RequestParam(value = "from", required = false) Instant from,
        @RequestParam(value = "to", required = false) Instant to,
        @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Instant end = to != null ? to : Instant.now().plus(1, ChronoUnit.MINUTES);
        Instant start = from != null ? from : end.minus(defaultSpan(bucket), ChronoUnit.SECONDS);
        LOG.debug("REST request for {} series at {} zoom over [{}, {})", metric, bucket, start, end);
        return ResponseEntity.ok(metricQueryService.series(metric, bucket, start, end, dimension));
    }

    /** {@code GET /api/admin/metrics/summary} : the KPI tiles. */
    @GetMapping("/summary")
    public ResponseEntity<MetricSummaryDTO> summary() {
        return ResponseEntity.ok(metricQueryService.summary());
    }

    /**
     * {@code POST /api/admin/metrics/rebuild} : recompute every rollup from the raw log.
     *
     * <p>Exists because rollups are a cache. After a data correction, or after any change to how a
     * metric is derived, this is how the numbers are brought back into agreement with the events
     * without waiting for the nightly job.
     */
    @PostMapping("/rebuild")
    public ResponseEntity<Integer> rebuild() {
        LOG.info("REST request to rebuild all metric rollups");
        return ResponseEntity.ok(metricRollupEngine.rebuildAll());
    }

    /** How much history to show by default at each zoom level. */
    private static long defaultSpan(BucketType bucket) {
        return switch (bucket) {
            case HOUR -> ChronoUnit.DAYS.getDuration().multipliedBy(2).toSeconds();
            case DAY -> ChronoUnit.DAYS.getDuration().multipliedBy(30).toSeconds();
            case WEEK -> ChronoUnit.DAYS.getDuration()
                .multipliedBy(26 * 7L)
                .toSeconds();
            case MONTH -> ChronoUnit.DAYS.getDuration()
                .multipliedBy(365 * 2L)
                .toSeconds();
            case QUARTER -> ChronoUnit.DAYS.getDuration()
                .multipliedBy(365 * 3L)
                .toSeconds();
            case YEAR -> ChronoUnit.DAYS.getDuration()
                .multipliedBy(365 * 5L)
                .toSeconds();
        };
    }
}
