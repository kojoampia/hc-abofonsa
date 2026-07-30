package net.jojoaddison.abofonsa.preview.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import net.jojoaddison.abofonsa.preview.IntegrationTest;
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import net.jojoaddison.abofonsa.preview.repository.CaptureEventRepository;
import net.jojoaddison.abofonsa.preview.repository.MetricRollupRepository;
import net.jojoaddison.abofonsa.preview.service.dto.MetricSeriesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the rollup engine.
 *
 * <p>These assert on bucket boundaries as much as on counts. A count that is merely wrong tends to
 * be noticed; a count filed under the wrong hour is not, and that is the failure this pipeline is
 * most prone to.
 */
@IntegrationTest
@Transactional
class MetricRollupEngineIT {

    @Autowired
    private MetricRollupEngine engine;

    @Autowired
    private MetricQueryService metricQueryService;

    @Autowired
    private CaptureEventRepository captureEventRepository;

    @Autowired
    private MetricRollupRepository metricRollupRepository;

    /** A fixed, timezone-unambiguous point in the past: 2026-03-10 09:30 UTC. */
    private static final Instant ANCHOR = LocalDate.of(2026, 3, 10).atTime(9, 30).toInstant(ZoneOffset.UTC);

    @BeforeEach
    void clearEvents() {
        captureEventRepository.deleteAll();
        metricRollupRepository.deleteAll();
    }

    /**
     * The regression that matters most.
     *
     * <p>{@code occurred_at} is a zone-less timestamp holding UTC, so reading it back through a JDBC
     * {@code Timestamp} interprets it in the JVM's default zone and shifts every bucket by the
     * server's offset. The aggregates therefore return an epoch number instead. If that ever
     * regresses, this assertion fails on any machine that is not on UTC — and passes on one that is,
     * which is exactly why it asserts an exact instant rather than a count.
     */
    @Test
    void bucketsLandOnUtcBoundariesRegardlessOfTheServerTimeZone() {
        persist(CaptureEventType.PAGE_VIEW, ANCHOR);

        engine.recompute(BucketType.HOUR, ANCHOR.minus(1, ChronoUnit.DAYS), ANCHOR.plus(1, ChronoUnit.DAYS));

        assertThat(metricRollupRepository.findAll())
            .filteredOn(rollup -> rollup.getMetricKey() == MetricKey.PAGE_VIEWS && rollup.getDimensionName() == null)
            .singleElement()
            .satisfies(rollup -> {
                // 09:30 UTC belongs to the 09:00 UTC hour. On a CEST server an unfixed conversion
                // files it under 07:00 or 11:00 — same count, wrong bucket.
                assertThat(rollup.getBucketStart()).isEqualTo(LocalDate.of(2026, 3, 10).atTime(9, 0).toInstant(ZoneOffset.UTC));
                assertThat(rollup.getBucketEnd()).isEqualTo(LocalDate.of(2026, 3, 10).atTime(10, 0).toInstant(ZoneOffset.UTC));
                assertThat(rollup.getValue()).isEqualTo(1L);
            });
    }

    @Test
    void countsEachMetricFromItsOwnEventType() {
        persist(CaptureEventType.PAGE_VIEW, ANCHOR);
        persist(CaptureEventType.PAGE_VIEW, ANCHOR.plusSeconds(60));
        persist(CaptureEventType.WAITLIST_SUBMIT, ANCHOR.plusSeconds(120));
        persist(CaptureEventType.PLEDGE_CTA_CLICK, ANCHOR.plusSeconds(180));

        engine.recompute(BucketType.DAY, ANCHOR.minus(1, ChronoUnit.DAYS), ANCHOR.plus(1, ChronoUnit.DAYS));

        assertThat(totalFor(MetricKey.PAGE_VIEWS)).isEqualTo(2);
        assertThat(totalFor(MetricKey.WAITLIST_SIGNUPS)).isEqualTo(1);
        assertThat(totalFor(MetricKey.PLEDGE_CLICKS)).isEqualTo(1);
    }

    /** Zooming out must not change the answer, only its resolution. */
    @Test
    void theSameEventsTotalTheSameAtEveryZoomLevel() {
        for (int day = 0; day < 5; day++) {
            for (int i = 0; i < 3; i++) {
                persist(CaptureEventType.PAGE_VIEW, ANCHOR.plus(day, ChronoUnit.DAYS).plusSeconds(i * 900L));
            }
        }

        Instant from = ANCHOR.minus(2, ChronoUnit.DAYS);
        Instant to = ANCHOR.plus(10, ChronoUnit.DAYS);

        for (BucketType bucketType : BucketType.values()) {
            engine.recompute(bucketType, MetricRollupEngine.floor(from, bucketType), to);
            MetricSeriesDTO series = metricQueryService.series(MetricKey.PAGE_VIEWS, bucketType, from, to, null);
            assertThat(series.series().getFirst().total()).as("total at %s zoom", bucketType).isEqualTo(15L);
        }
    }

    /**
     * A quiet bucket must be a zero, not a missing point — a line chart drawn from sparse data
     * joins across the gap and shows activity that did not happen.
     */
    @Test
    void quietBucketsComeBackAsExplicitZeroes() {
        persist(CaptureEventType.PAGE_VIEW, ANCHOR);
        persist(CaptureEventType.PAGE_VIEW, ANCHOR.plus(3, ChronoUnit.DAYS));

        Instant from = ANCHOR.minus(1, ChronoUnit.DAYS);
        Instant to = ANCHOR.plus(5, ChronoUnit.DAYS);
        engine.recompute(BucketType.DAY, MetricRollupEngine.floor(from, BucketType.DAY), to);

        MetricSeriesDTO.Series series = metricQueryService.series(MetricKey.PAGE_VIEWS, BucketType.DAY, from, to, null).series().getFirst();

        // [09 Mar 00:00, 15 Mar 09:30) covers seven whole day-buckets — the 15th counts, because its
        // bucket starts before `to`. Events fall on the 10th and the 13th.
        assertThat(series.points()).hasSize(7);
        assertThat(series.points()).extracting(MetricSeriesDTO.Point::value).containsExactly(0L, 1L, 0L, 0L, 1L, 0L, 0L);
        assertThat(series.total()).isEqualTo(2L);
    }

    /** Splitting by a facet must partition the total, not duplicate or drop any of it. */
    @Test
    void facetsPartitionTheTotal() {
        persistWithSource(ANCHOR, "newsletter");
        persistWithSource(ANCHOR.plusSeconds(60), "newsletter");
        persistWithSource(ANCHOR.plusSeconds(120), "x");
        persistWithSource(ANCHOR.plusSeconds(180), null);

        Instant from = ANCHOR.minus(1, ChronoUnit.DAYS);
        Instant to = ANCHOR.plus(1, ChronoUnit.DAYS);
        engine.recompute(BucketType.DAY, MetricRollupEngine.floor(from, BucketType.DAY), to);

        MetricSeriesDTO faceted = metricQueryService.series(MetricKey.PAGE_VIEWS, BucketType.DAY, from, to, "utmSource");
        MetricSeriesDTO total = metricQueryService.series(MetricKey.PAGE_VIEWS, BucketType.DAY, from, to, null);

        assertThat(faceted.series()).hasSize(3);
        assertThat(faceted.series().stream().mapToLong(MetricSeriesDTO.Series::total).sum()).isEqualTo(total.series().getFirst().total());
        // Sorted by size, so the biggest facet leads.
        assertThat(faceted.series().getFirst().label()).isEqualTo("newsletter");
        assertThat(faceted.series()).extracting(MetricSeriesDTO.Series::label).contains("(none)");
    }

    /** Recomputing must overwrite a window, never append a second copy of it. */
    @Test
    void recomputingAWindowIsIdempotent() {
        persist(CaptureEventType.PAGE_VIEW, ANCHOR);
        Instant from = MetricRollupEngine.floor(ANCHOR.minus(1, ChronoUnit.DAYS), BucketType.DAY);
        Instant to = ANCHOR.plus(1, ChronoUnit.DAYS);

        engine.recompute(BucketType.DAY, from, to);
        long afterFirst = metricRollupRepository.count();
        engine.recompute(BucketType.DAY, from, to);

        assertThat(metricRollupRepository.count()).isEqualTo(afterFirst);
        assertThat(totalFor(MetricKey.PAGE_VIEWS)).isEqualTo(1);
    }

    /** An over-wide request is clamped, and says so rather than returning a partial total silently. */
    @Test
    void anOverWideRequestReportsThatItWasTruncated() {
        persist(CaptureEventType.PAGE_VIEW, ANCHOR);
        Instant to = ANCHOR.plus(1, ChronoUnit.DAYS);
        engine.recompute(BucketType.HOUR, MetricRollupEngine.floor(ANCHOR.minus(1, ChronoUnit.DAYS), BucketType.HOUR), to);

        MetricSeriesDTO wide = metricQueryService.series(
            MetricKey.PAGE_VIEWS,
            BucketType.HOUR,
            ANCHOR.minus(3650, ChronoUnit.DAYS),
            to,
            null
        );
        MetricSeriesDTO narrow = metricQueryService.series(
            MetricKey.PAGE_VIEWS,
            BucketType.HOUR,
            ANCHOR.minus(1, ChronoUnit.DAYS),
            to,
            null
        );

        assertThat(wide.truncated()).isTrue();
        assertThat(wide.from()).isAfter(ANCHOR.minus(3650, ChronoUnit.DAYS));
        assertThat(narrow.truncated()).isFalse();
    }

    @Test
    void weekBucketsStartOnMondayToMatchPostgres() {
        // 2026-03-10 is a Tuesday; its week starts Monday the 9th.
        assertThat(MetricRollupEngine.floor(ANCHOR, BucketType.WEEK)).isEqualTo(
            LocalDate.of(2026, 3, 9).atStartOfDay().toInstant(ZoneOffset.UTC)
        );
    }

    private long totalFor(MetricKey metricKey) {
        return metricRollupRepository
            .findAll()
            .stream()
            .filter(rollup -> rollup.getMetricKey() == metricKey && rollup.getDimensionName() == null)
            .mapToLong(rollup -> rollup.getValue())
            .sum();
    }

    private void persist(CaptureEventType type, Instant at) {
        persist(type, at, null);
    }

    private void persistWithSource(Instant at, String utmSource) {
        persist(CaptureEventType.PAGE_VIEW, at, utmSource);
    }

    private void persist(CaptureEventType type, Instant at, String utmSource) {
        CaptureEvent event = new CaptureEvent();
        event.setEventType(type);
        event.setOccurredAt(at);
        event.setOccurredDate(at.atZone(ZoneOffset.UTC).toLocalDate());
        event.setSessionHash("session-" + at.toEpochMilli());
        event.setUtmSource(utmSource);
        captureEventRepository.saveAndFlush(event);
    }
}
