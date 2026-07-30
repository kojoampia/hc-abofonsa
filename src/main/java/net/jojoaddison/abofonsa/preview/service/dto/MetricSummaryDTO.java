package net.jojoaddison.abofonsa.preview.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/** The dashboard's KPI tiles. */
public record MetricSummaryDTO(
    Instant generatedAt,
    long totalSignups,
    long confirmedSignups,
    long unsubscribed,
    List<Tile> tiles
) implements Serializable {
    /**
     * A headline number with its period-over-period movement.
     *
     * <p>{@code previous} is the immediately preceding window of the same length, so "last 7 days"
     * compares against the 7 days before that. {@code changePercent} is null rather than zero or
     * infinity when the previous window was empty — there is no meaningful percentage change from
     * nothing, and rendering "+100%" for the first week of a launch would be actively misleading.
     */
    public record Tile(String key, long current, long previous, Double changePercent) implements Serializable {}
}
