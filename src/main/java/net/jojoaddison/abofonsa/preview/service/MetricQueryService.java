package net.jojoaddison.abofonsa.preview.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSeriesDTO;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads rollups into the shapes the dashboard draws.
 *
 * <p>Reads only. Everything here comes from {@link MetricRollupEngine}'s output, so a slow or
 * failed rollup shows stale numbers rather than wrong ones.
 */
@Service
@Transactional(readOnly = true)
public class MetricQueryService {

    private static final String TOTAL_SERIES = "total";

    /**
     * Most buckets one response may carry. Not a performance number so much as a sanity one: an
     * HOUR series over five years is a mis-set filter, not a request worth serving.
     */
    private static final int MAX_POINTS = 1500;

    private static final Logger LOG = LoggerFactory.getLogger(MetricQueryService.class);

    private final MetricRollupRepository metricRollupRepository;
    private final WaitlistSignupRepository waitlistSignupRepository;

    public MetricQueryService(MetricRollupRepository metricRollupRepository, WaitlistSignupRepository waitlistSignupRepository) {
        this.metricRollupRepository = metricRollupRepository;
        this.waitlistSignupRepository = waitlistSignupRepository;
    }

    /**
     * One metric at one zoom level over {@code [from, to)}, optionally split by a facet.
     */
    public MetricSeriesDTO series(MetricKey metricKey, BucketType bucketType, Instant from, Instant to, String dimensionName) {
        Instant requestedFrom = MetricRollupEngine.floor(from, bucketType);
        Instant alignedFrom = clampToMaxPoints(requestedFrom, to, bucketType);
        boolean truncated = alignedFrom.isAfter(requestedFrom);

        if (truncated) {
            LOG.info(
                "{} at {} zoom over [{}, {}) exceeds {} buckets; returning the most recent window from {}",
                metricKey,
                bucketType,
                requestedFrom,
                to,
                MAX_POINTS,
                alignedFrom
            );
        }

        if (dimensionName == null || dimensionName.isBlank()) {
            List<MetricRollup> rows = metricRollupRepository.findSeries(metricKey, bucketType, alignedFrom, to);
            List<MetricSeriesDTO.Point> points = fill(rows, bucketType, alignedFrom, to);
            long total = points.stream().mapToLong(MetricSeriesDTO.Point::value).sum();
            return new MetricSeriesDTO(
                metricKey.name(),
                bucketType.name(),
                alignedFrom,
                to,
                null,
                truncated,
                List.of(new MetricSeriesDTO.Series(TOTAL_SERIES, total, points))
            );
        }

        List<MetricRollup> rows = metricRollupRepository.findSeriesByDimension(metricKey, bucketType, alignedFrom, to, dimensionName);

        // Group by facet value first — each becomes its own line, and each is gap-filled separately
        // so the lines share an x-axis even where one of them had no events.
        Map<String, List<MetricRollup>> byValue = new LinkedHashMap<>();
        rows.forEach(row -> byValue.computeIfAbsent(row.getDimensionValue(), key -> new ArrayList<>()).add(row));

        List<MetricSeriesDTO.Series> series = byValue
            .entrySet()
            .stream()
            .map(entry -> {
                List<MetricSeriesDTO.Point> points = fill(entry.getValue(), bucketType, alignedFrom, to);
                return new MetricSeriesDTO.Series(entry.getKey(), points.stream().mapToLong(MetricSeriesDTO.Point::value).sum(), points);
            })
            .sorted((a, b) -> Long.compare(b.total(), a.total()))
            .toList();

        return new MetricSeriesDTO(metricKey.name(), bucketType.name(), alignedFrom, to, dimensionName, truncated, series);
    }

    /** KPI tiles: lifetime counts plus each metric's last 7 days against the 7 before that. */
    public MetricSummaryDTO summary() {
        Instant now = Instant.now();
        Instant currentFrom = MetricRollupEngine.floor(now.minus(6, ChronoUnit.DAYS), BucketType.DAY);
        Instant previousFrom = MetricRollupEngine.floor(currentFrom.minus(7, ChronoUnit.DAYS), BucketType.DAY);
        Instant to = MetricRollupEngine.floor(now, BucketType.DAY).plus(1, ChronoUnit.DAYS);

        List<MetricSummaryDTO.Tile> tiles = List.of(
            tile(MetricKey.WAITLIST_SIGNUPS, previousFrom, currentFrom, to),
            tile(MetricKey.WAITLIST_CONFIRMED, previousFrom, currentFrom, to),
            tile(MetricKey.PLEDGE_CLICKS, previousFrom, currentFrom, to),
            tile(MetricKey.PAGE_VIEWS, previousFrom, currentFrom, to),
            tile(MetricKey.UNIQUE_VISITORS, previousFrom, currentFrom, to)
        );

        // Lifetime totals come from the signup table, not the rollups: these are counts of rows that
        // exist now, and an unsubscribe changes a row's state without producing a new event.
        return new MetricSummaryDTO(
            now,
            waitlistSignupRepository.count(),
            waitlistSignupRepository.countByStatus(SignupStatus.CONFIRMED),
            waitlistSignupRepository.countByStatus(SignupStatus.UNSUBSCRIBED),
            tiles
        );
    }

    private MetricSummaryDTO.Tile tile(MetricKey metricKey, Instant previousFrom, Instant currentFrom, Instant to) {
        long current = metricRollupRepository.sumOverWindow(metricKey, BucketType.DAY, currentFrom, to);
        long previous = metricRollupRepository.sumOverWindow(metricKey, BucketType.DAY, previousFrom, currentFrom);
        // Null, not zero and not infinity: there is no meaningful percentage change from nothing,
        // and "+100%" on a launch's first week would read as growth that did not happen.
        Double change = previous == 0 ? null : ((double) (current - previous) / previous) * 100d;
        return new MetricSummaryDTO.Tile(metricKey.name(), current, previous, change);
    }

    /**
     * Turns sparse rollup rows into a dense point-per-bucket series.
     *
     * <p>A quiet bucket has no row at all — nothing happened, so nothing was counted. Left sparse,
     * a line chart joins the two surrounding points and draws sustained activity straight through
     * the gap.
     */
    private List<MetricSeriesDTO.Point> fill(List<MetricRollup> rows, BucketType bucketType, Instant from, Instant to) {
        Map<Instant, Long> byBucket = new LinkedHashMap<>();
        rows.forEach(row -> byBucket.merge(row.getBucketStart(), row.getValue(), Long::sum));

        List<MetricSeriesDTO.Point> points = new ArrayList<>();
        Instant cursor = from;
        // No guard here: series() has already bounded the window to MAX_POINTS, and a second silent
        // cap at this level is exactly what made a truncated series look complete.
        while (cursor.isBefore(to)) {
            Instant next = MetricRollupEngine.next(cursor, bucketType);
            points.add(new MetricSeriesDTO.Point(cursor, next, byBucket.getOrDefault(cursor, 0L)));
            cursor = next;
        }
        return points;
    }

    /**
     * Moves {@code from} forward until the window holds at most {@link #MAX_POINTS} buckets.
     *
     * <p>Keeps the most recent window rather than the oldest: somebody who over-asks at HOUR zoom
     * wants recent detail, not five years ago.
     */
    private static Instant clampToMaxPoints(Instant from, Instant to, BucketType bucketType) {
        Instant cursor = to;
        for (int i = 0; i < MAX_POINTS; i++) {
            Instant previous = MetricRollupEngine.floor(cursor.minusMillis(1), bucketType);
            if (!previous.isAfter(from)) {
                return from;
            }
            cursor = previous;
        }
        return cursor;
    }
}
