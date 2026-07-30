package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.MetricRollup} entity.
 */
@Schema(
    description = "Pre-aggregated counts, one row per (metric, granularity, bucket, dimension). Lets the\ndashboard zoom from hour to year without scanning CaptureEvent, and lets an export of\n'captures per month' be a single indexed read."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricRollupDTO implements Serializable {

    private Long id;

    @NotNull
    private MetricKey metricKey;

    @NotNull
    private BucketType bucketType;

    @NotNull
    private Instant bucketStart;

    @NotNull
    private Instant bucketEnd;

    @Size(max = 60)
    @Schema(description = "null on the total row; otherwise the facet being split by, e.g. 'utmSource'.")
    private String dimensionName;

    @Size(max = 160)
    private String dimensionValue;

    @NotNull
    @Min(value = 0L)
    private Long value;

    @NotNull
    private Instant computedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetricKey getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(MetricKey metricKey) {
        this.metricKey = metricKey;
    }

    public BucketType getBucketType() {
        return bucketType;
    }

    public void setBucketType(BucketType bucketType) {
        this.bucketType = bucketType;
    }

    public Instant getBucketStart() {
        return bucketStart;
    }

    public void setBucketStart(Instant bucketStart) {
        this.bucketStart = bucketStart;
    }

    public Instant getBucketEnd() {
        return bucketEnd;
    }

    public void setBucketEnd(Instant bucketEnd) {
        this.bucketEnd = bucketEnd;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Instant getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(Instant computedAt) {
        this.computedAt = computedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricRollupDTO)) {
            return false;
        }

        MetricRollupDTO metricRollupDTO = (MetricRollupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, metricRollupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricRollupDTO{" +
            "id=" + getId() +
            ", metricKey='" + getMetricKey() + "'" +
            ", bucketType='" + getBucketType() + "'" +
            ", bucketStart='" + getBucketStart() + "'" +
            ", bucketEnd='" + getBucketEnd() + "'" +
            ", dimensionName='" + getDimensionName() + "'" +
            ", dimensionValue='" + getDimensionValue() + "'" +
            ", value=" + getValue() +
            ", computedAt='" + getComputedAt() + "'" +
            "}";
    }
}
