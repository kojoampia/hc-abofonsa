package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Pre-aggregated counts, one row per (metric, granularity, bucket, dimension). Lets the
 * dashboard zoom from hour to year without scanning CaptureEvent, and lets an export of
 * 'captures per month' be a single indexed read.
 */
@Entity
@Table(name = "metric_rollup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricRollup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_key", nullable = false)
    private MetricKey metricKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_type", nullable = false)
    private BucketType bucketType;

    @NotNull
    @Column(name = "bucket_start", nullable = false)
    private Instant bucketStart;

    @NotNull
    @Column(name = "bucket_end", nullable = false)
    private Instant bucketEnd;

    /**
     * null on the total row; otherwise the facet being split by, e.g. 'utmSource'.
     */
    @Size(max = 60)
    @Column(name = "dimension_name", length = 60)
    private String dimensionName;

    @Size(max = 160)
    @Column(name = "dimension_value", length = 160)
    private String dimensionValue;

    @NotNull
    @Min(value = 0L)
    @Column(name = "value", nullable = false)
    private Long value;

    @NotNull
    @Column(name = "computed_at", nullable = false)
    private Instant computedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MetricRollup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetricKey getMetricKey() {
        return this.metricKey;
    }

    public MetricRollup metricKey(MetricKey metricKey) {
        this.setMetricKey(metricKey);
        return this;
    }

    public void setMetricKey(MetricKey metricKey) {
        this.metricKey = metricKey;
    }

    public BucketType getBucketType() {
        return this.bucketType;
    }

    public MetricRollup bucketType(BucketType bucketType) {
        this.setBucketType(bucketType);
        return this;
    }

    public void setBucketType(BucketType bucketType) {
        this.bucketType = bucketType;
    }

    public Instant getBucketStart() {
        return this.bucketStart;
    }

    public MetricRollup bucketStart(Instant bucketStart) {
        this.setBucketStart(bucketStart);
        return this;
    }

    public void setBucketStart(Instant bucketStart) {
        this.bucketStart = bucketStart;
    }

    public Instant getBucketEnd() {
        return this.bucketEnd;
    }

    public MetricRollup bucketEnd(Instant bucketEnd) {
        this.setBucketEnd(bucketEnd);
        return this;
    }

    public void setBucketEnd(Instant bucketEnd) {
        this.bucketEnd = bucketEnd;
    }

    public String getDimensionName() {
        return this.dimensionName;
    }

    public MetricRollup dimensionName(String dimensionName) {
        this.setDimensionName(dimensionName);
        return this;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getDimensionValue() {
        return this.dimensionValue;
    }

    public MetricRollup dimensionValue(String dimensionValue) {
        this.setDimensionValue(dimensionValue);
        return this;
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public Long getValue() {
        return this.value;
    }

    public MetricRollup value(Long value) {
        this.setValue(value);
        return this;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Instant getComputedAt() {
        return this.computedAt;
    }

    public MetricRollup computedAt(Instant computedAt) {
        this.setComputedAt(computedAt);
        return this;
    }

    public void setComputedAt(Instant computedAt) {
        this.computedAt = computedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricRollup)) {
            return false;
        }
        return getId() != null && getId().equals(((MetricRollup) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricRollup{" +
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
