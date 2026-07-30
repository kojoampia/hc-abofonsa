package net.jojoaddison.abofonsa.preview.repository;

import java.time.Instant;
import java.util.List;
import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CaptureEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CaptureEventRepository extends JpaRepository<CaptureEvent, Long>, JpaSpecificationExecutor<CaptureEvent> {
    /**
     * Counts events of one type per time bucket.
     *
     * <p>Native, because {@code date_trunc} has no JPQL equivalent and the alternative — loading
     * every event and bucketing in Java — is the thing the rollup table exists to avoid.
     *
     * <p>{@code occurred_at} is a {@code timestamp without time zone} holding UTC (Hibernate writes
     * Instants that way), so no conversion is needed and the buckets are UTC throughout. Rendering
     * them in Africa/Accra is the dashboard's job, not the aggregate's.
     *
     * <p>The unit is a bind parameter rather than string-concatenated: {@code date_trunc} takes its
     * unit as text, so this is both injection-safe and reusable across every zoom level.
     */
    @Query(
        nativeQuery = true,
        value = """
        select extract(epoch from date_trunc(cast(:unit as text), occurred_at)) as bucket_epoch, count(*) as total
        from capture_event
        where event_type = cast(:eventType as text)
          and occurred_at >= :from
          and occurred_at < :to
        group by 1
        order by 1
        """
    )
    List<Object[]> countByBucket(
        @Param("unit") String unit,
        @Param("eventType") String eventType,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    /**
     * Distinct session hashes per bucket — the UNIQUE_VISITORS series.
     *
     * <p>Only meaningful at DAY granularity in the way people expect: the hash rotates at UTC
     * midnight, so a visitor present on two days counts twice inside a MONTH bucket. That is a
     * deliberate trade for not holding a durable identifier, and the series is labelled to say so.
     */
    @Query(
        nativeQuery = true,
        value = """
        select extract(epoch from date_trunc(cast(:unit as text), occurred_at)) as bucket_epoch, count(distinct session_hash) as total
        from capture_event
        where session_hash is not null
          and occurred_at >= :from
          and occurred_at < :to
        group by 1
        order by 1
        """
    )
    List<Object[]> countDistinctSessionsByBucket(@Param("unit") String unit, @Param("from") Instant from, @Param("to") Instant to);

    /** Splits one event type by campaign source, for the drill-down facets. */
    @Query(
        nativeQuery = true,
        value = """
        select extract(epoch from date_trunc(cast(:unit as text), occurred_at)) as bucket_epoch,
               coalesce(utm_source, '(none)') as dimension_value,
               count(*) as total
        from capture_event
        where event_type = cast(:eventType as text)
          and occurred_at >= :from
          and occurred_at < :to
        group by 1, 2
        order by 1, 2
        """
    )
    List<Object[]> countByBucketAndUtmSource(
        @Param("unit") String unit,
        @Param("eventType") String eventType,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    /** Oldest event, so a full backfill can bound itself instead of guessing a start date. */
    @Query("select min(e.occurredAt) from CaptureEvent e")
    Instant findEarliestOccurredAt();
}
