package net.jojoaddison.abofonsa.preview.repository;

import java.time.Instant;
import java.util.List;
import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MetricRollup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricRollupRepository extends JpaRepository<MetricRollup, Long>, JpaSpecificationExecutor<MetricRollup> {
    /**
     * The series behind a chart: one metric at one zoom level over a window, totals only.
     *
     * <p>{@code dimensionName is null} selects the total row rather than the per-facet splits, which
     * share the same (metric, bucket) key. Omitting it would silently sum the facets into the total
     * and double every number on the chart.
     */
    @Query(
        """
        select r from MetricRollup r
        where r.metricKey = :metricKey
          and r.bucketType = :bucketType
          and r.bucketStart >= :from
          and r.bucketStart < :to
          and r.dimensionName is null
        order by r.bucketStart
        """
    )
    List<MetricRollup> findSeries(
        @Param("metricKey") MetricKey metricKey,
        @Param("bucketType") BucketType bucketType,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    /** The same window split by one facet, for the drill-down. */
    @Query(
        """
        select r from MetricRollup r
        where r.metricKey = :metricKey
          and r.bucketType = :bucketType
          and r.bucketStart >= :from
          and r.bucketStart < :to
          and r.dimensionName = :dimensionName
        order by r.bucketStart, r.dimensionValue
        """
    )
    List<MetricRollup> findSeriesByDimension(
        @Param("metricKey") MetricKey metricKey,
        @Param("bucketType") BucketType bucketType,
        @Param("from") Instant from,
        @Param("to") Instant to,
        @Param("dimensionName") String dimensionName
    );

    /**
     * Clears a window before it is recomputed.
     *
     * <p>Delete-then-insert rather than upsert: a bucket whose events were all deleted has no row
     * in the new result set at all, so an upsert would leave the stale value behind forever.
     */
    @Modifying
    @Query(
        """
        delete from MetricRollup r
        where r.bucketType = :bucketType
          and r.bucketStart >= :from
          and r.bucketStart < :to
        """
    )
    int deleteWindow(@Param("bucketType") BucketType bucketType, @Param("from") Instant from, @Param("to") Instant to);

    @Query(
        """
        select coalesce(sum(r.value), 0) from MetricRollup r
        where r.metricKey = :metricKey
          and r.bucketType = :bucketType
          and r.bucketStart >= :from
          and r.bucketStart < :to
          and r.dimensionName is null
        """
    )
    long sumOverWindow(
        @Param("metricKey") MetricKey metricKey,
        @Param("bucketType") BucketType bucketType,
        @Param("from") Instant from,
        @Param("to") Instant to
    );
}
