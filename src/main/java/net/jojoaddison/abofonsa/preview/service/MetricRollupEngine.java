package net.jojoaddison.abofonsa.preview.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.repository.WaitlistSignupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Derives {@link MetricRollup} rows from the raw {@code CaptureEvent} log.
 *
 * <p>Named "Engine" because {@code MetricRollupService} is taken — that is JHipster's generated CRUD
 * service for the entity. This is the thing that computes the rows; that one just reads and writes
 * them for the admin screens.
 *
 * <p>Rollups are a cache, never a source of truth: every value here is recomputable from
 * {@code capture_event} and {@code waitlist_signup}, which is what makes it safe to delete a window
 * and rebuild it. If a rollup ever disagrees with the raw log, the log wins.
 */
@Service
public class MetricRollupEngine {

    private static final Logger LOG = LoggerFactory.getLogger(MetricRollupEngine.class);

    /**
     * Which raw event backs each straightforward counting metric. The two metrics not listed here
     * are computed differently: UNIQUE_VISITORS is a distinct count, and WAITLIST_UNSUBSCRIBED comes
     * from the signup table because unsubscribing is a state change rather than a page interaction.
     */
    private static final Map<MetricKey, CaptureEventType> EVENT_BACKED = Map.of(
        MetricKey.WAITLIST_SIGNUPS,
        CaptureEventType.WAITLIST_SUBMIT,
        MetricKey.WAITLIST_CONFIRMED,
        CaptureEventType.WAITLIST_CONFIRM,
        MetricKey.PLEDGE_CLICKS,
        CaptureEventType.PLEDGE_CTA_CLICK,
        MetricKey.PAGE_VIEWS,
        CaptureEventType.PAGE_VIEW
    );

    /** The facet the drill-down splits by. One for now; the column is general. */
    private static final String DIMENSION_UTM_SOURCE = "utmSource";

    private final CaptureEventRepository captureEventRepository;
    private final WaitlistSignupRepository waitlistSignupRepository;
    private final MetricRollupRepository metricRollupRepository;

    public MetricRollupEngine(
        CaptureEventRepository captureEventRepository,
        WaitlistSignupRepository waitlistSignupRepository,
        MetricRollupRepository metricRollupRepository
    ) {
        this.captureEventRepository = captureEventRepository;
        this.waitlistSignupRepository = waitlistSignupRepository;
        this.metricRollupRepository = metricRollupRepository;
    }

    /**
     * Recomputes every metric at one granularity over a half-open window {@code [from, to)}.
     *
     * @return how many rollup rows the window now holds
     */
    @Transactional
    public int recompute(BucketType bucketType, Instant from, Instant to) {
        String unit = truncUnit(bucketType);
        metricRollupRepository.deleteWindow(bucketType, from, to);

        Instant computedAt = Instant.now();
        List<MetricRollup> rows = new ArrayList<>();

        EVENT_BACKED.forEach((metricKey, eventType) ->
            captureEventRepository
                .countByBucket(unit, eventType.name(), from, to)
                .forEach(row -> rows.add(rollup(metricKey, bucketType, bucketStart(row), null, null, count(row, 1), computedAt)))
        );

        captureEventRepository
            .countDistinctSessionsByBucket(unit, from, to)
            .forEach(row ->
                rows.add(rollup(MetricKey.UNIQUE_VISITORS, bucketType, bucketStart(row), null, null, count(row, 1), computedAt))
            );

        waitlistSignupRepository
            .countUnsubscribesByBucket(unit, from, to)
            .forEach(row ->
                rows.add(rollup(MetricKey.WAITLIST_UNSUBSCRIBED, bucketType, bucketStart(row), null, null, count(row, 1), computedAt))
            );

        // Campaign attribution: only for the metrics a marketer would actually attribute.
        List.of(MetricKey.WAITLIST_SIGNUPS, MetricKey.PLEDGE_CLICKS, MetricKey.PAGE_VIEWS).forEach(metricKey ->
            captureEventRepository
                .countByBucketAndUtmSource(unit, EVENT_BACKED.get(metricKey).name(), from, to)
                .forEach(row ->
                    rows.add(
                        rollup(metricKey, bucketType, bucketStart(row), DIMENSION_UTM_SOURCE, (String) row[1], count(row, 2), computedAt)
                    )
                )
        );

        metricRollupRepository.saveAll(rows);
        LOG.debug("Recomputed {} {} rollup rows for [{}, {})", rows.size(), bucketType, from, to);
        return rows.size();
    }

    /**
     * Rebuilds every granularity from the first recorded event to now.
     *
     * <p>Used on startup so a fresh deployment is not a blank dashboard, and available to re-run by
     * hand after a data fix.
     */
    @Transactional
    public int rebuildAll() {
        Instant earliest = captureEventRepository.findEarliestOccurredAt();
        if (earliest == null) {
            LOG.debug("No capture events yet; nothing to roll up");
            return 0;
        }
        // The window must start at the containing bucket's boundary, not at the first event: a
        // YEAR bucket starting mid-January would never match the date_trunc'd bucket_start the
        // queries return, and the row would be orphaned from every series that reads it.
        Instant from = floor(earliest, BucketType.YEAR);
        Instant to = Instant.now().plus(1, ChronoUnit.DAYS);

        int total = 0;
        for (BucketType bucketType : BucketType.values()) {
            total += recompute(bucketType, floor(from, bucketType), to);
        }
        LOG.info("Rebuilt {} rollup rows across all granularities from {}", total, from);
        return total;
    }

    /** Postgres {@code date_trunc} unit for a bucket type. */
    public static String truncUnit(BucketType bucketType) {
        return switch (bucketType) {
            case HOUR -> "hour";
            case DAY -> "day";
            case WEEK -> "week";
            case MONTH -> "month";
            case QUARTER -> "quarter";
            case YEAR -> "year";
        };
    }

    /**
     * Start of the bucket containing an instant, in UTC — the Java mirror of {@code date_trunc}.
     *
     * <p>WEEK truncates to Monday, matching Postgres. Getting this wrong would not throw; it would
     * quietly place rows on boundaries the queries never look for.
     */
    public static Instant floor(Instant instant, BucketType bucketType) {
        ZonedDateTime utc = instant.atZone(ZoneOffset.UTC);
        return switch (bucketType) {
            case HOUR -> utc.truncatedTo(ChronoUnit.HOURS).toInstant();
            case DAY -> utc.truncatedTo(ChronoUnit.DAYS).toInstant();
            case WEEK -> utc.truncatedTo(ChronoUnit.DAYS).with(java.time.DayOfWeek.MONDAY).toInstant();
            case MONTH -> utc.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).toInstant();
            case QUARTER -> utc.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).with(IsoFields.DAY_OF_QUARTER, 1).toInstant();
            case YEAR -> utc.truncatedTo(ChronoUnit.DAYS).withDayOfYear(1).toInstant();
        };
    }

    /** Exclusive end of the bucket that starts at {@code bucketStart}. */
    public static Instant next(Instant bucketStart, BucketType bucketType) {
        ZonedDateTime utc = bucketStart.atZone(ZoneOffset.UTC);
        return switch (bucketType) {
            case HOUR -> utc.plusHours(1).toInstant();
            case DAY -> utc.plusDays(1).toInstant();
            case WEEK -> utc.plusWeeks(1).toInstant();
            case MONTH -> utc.plusMonths(1).toInstant();
            case QUARTER -> utc.plusMonths(3).toInstant();
            case YEAR -> utc.plusYears(1).toInstant();
        };
    }

    private MetricRollup rollup(
        MetricKey metricKey,
        BucketType bucketType,
        Instant bucketStart,
        String dimensionName,
        String dimensionValue,
        long value,
        Instant computedAt
    ) {
        MetricRollup rollup = new MetricRollup();
        rollup.setMetricKey(metricKey);
        rollup.setBucketType(bucketType);
        rollup.setBucketStart(bucketStart);
        rollup.setBucketEnd(next(bucketStart, bucketType));
        rollup.setDimensionName(dimensionName);
        rollup.setDimensionValue(dimensionValue);
        rollup.setValue(value);
        rollup.setComputedAt(computedAt);
        return rollup;
    }

    /**
     * Converts the query's {@code bucket_epoch} into an Instant.
     *
     * <p>The queries return {@code extract(epoch from ...)} — a number — rather than the timestamp
     * itself, and that choice is the whole defence against a class of bug that does not announce
     * itself. {@code occurred_at} is a {@code timestamp without time zone} holding UTC. Ask JDBC
     * for it as a {@link Timestamp} and the driver, seeing no zone on the column, interprets that
     * wall-clock in the JVM's default zone; the resulting Instant is off by the server's offset,
     * and every bucket on every chart shifts with it. It was two hours here, which is small enough
     * to pass for real data while quietly moving points across midnight into the wrong day.
     *
     * <p>Postgres defines {@code extract(epoch ...)} on a zone-less timestamp as seconds since the
     * Unix epoch treating the value as UTC — precisely what it holds. A number has no zone to
     * misinterpret, so nothing between the database and this line can shift it.
     */
    private static Instant bucketStart(Object[] row) {
        Object value = row[0];
        if (value instanceof Number epochSeconds) {
            return Instant.ofEpochMilli(Math.round(epochSeconds.doubleValue() * 1000d));
        }
        throw new IllegalStateException("Unexpected bucket_epoch type: " + value.getClass());
    }

    private static long count(Object[] row, int index) {
        return ((Number) row[index]).longValue();
    }
}
