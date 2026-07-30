package net.jojoaddison.abofonsa.preview.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * One chartable series.
 *
 * <p>Buckets with no events are filled in with zero rather than omitted. A chart drawn from sparse
 * points draws a straight line across a quiet week and reads as sustained activity; an explicit
 * zero reads as the quiet week it was.
 */
public record MetricSeriesDTO(
    String metricKey,
    String bucketType,
    /** The window actually returned, which is not always the window asked for — see {@link #truncated}. */
    Instant from,
    Instant to,
    /** null for the total series; otherwise the facet this was split by. */
    String dimensionName,
    /**
     * True when the requested range held more buckets than one response may carry, and {@code from}
     * was moved forward to the most recent whole window that fits.
     *
     * <p>Reported rather than silently applied. An hourly series over seven months quietly cut off
     * partway still renders as a complete chart with an authoritative-looking total, and the total
     * is wrong — which is worse than refusing, and much worse than saying so.
     */
    boolean truncated,
    List<Series> series
) implements Serializable {
    public record Point(Instant bucketStart, Instant bucketEnd, long value) implements Serializable {}

    /** A single line. The total series has exactly one, named "total". */
    public record Series(String label, long total, List<Point> points) implements Serializable {}
}
