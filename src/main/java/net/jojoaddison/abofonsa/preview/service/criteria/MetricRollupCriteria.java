package net.jojoaddison.abofonsa.preview.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.MetricKey;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link net.jojoaddison.abofonsa.preview.domain.MetricRollup} entity. This class is used
 * in {@link net.jojoaddison.abofonsa.preview.web.rest.MetricRollupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /metric-rollups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricRollupCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MetricKey
     */
    public static class MetricKeyFilter extends Filter<MetricKey> {

        public MetricKeyFilter() {}

        public MetricKeyFilter(MetricKeyFilter filter) {
            super(filter);
        }

        @Override
        public MetricKeyFilter copy() {
            return new MetricKeyFilter(this);
        }
    }

    /**
     * Class for filtering BucketType
     */
    public static class BucketTypeFilter extends Filter<BucketType> {

        public BucketTypeFilter() {}

        public BucketTypeFilter(BucketTypeFilter filter) {
            super(filter);
        }

        @Override
        public BucketTypeFilter copy() {
            return new BucketTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private MetricKeyFilter metricKey;

    private BucketTypeFilter bucketType;

    private InstantFilter bucketStart;

    private InstantFilter bucketEnd;

    private StringFilter dimensionName;

    private StringFilter dimensionValue;

    private LongFilter value;

    private InstantFilter computedAt;

    private Boolean distinct;

    public MetricRollupCriteria() {}

    public MetricRollupCriteria(MetricRollupCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.metricKey = other.optionalMetricKey().map(MetricKeyFilter::copy).orElse(null);
        this.bucketType = other.optionalBucketType().map(BucketTypeFilter::copy).orElse(null);
        this.bucketStart = other.optionalBucketStart().map(InstantFilter::copy).orElse(null);
        this.bucketEnd = other.optionalBucketEnd().map(InstantFilter::copy).orElse(null);
        this.dimensionName = other.optionalDimensionName().map(StringFilter::copy).orElse(null);
        this.dimensionValue = other.optionalDimensionValue().map(StringFilter::copy).orElse(null);
        this.value = other.optionalValue().map(LongFilter::copy).orElse(null);
        this.computedAt = other.optionalComputedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MetricRollupCriteria copy() {
        return new MetricRollupCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public MetricKeyFilter getMetricKey() {
        return metricKey;
    }

    public Optional<MetricKeyFilter> optionalMetricKey() {
        return Optional.ofNullable(metricKey);
    }

    public MetricKeyFilter metricKey() {
        if (metricKey == null) {
            setMetricKey(new MetricKeyFilter());
        }
        return metricKey;
    }

    public void setMetricKey(MetricKeyFilter metricKey) {
        this.metricKey = metricKey;
    }

    public BucketTypeFilter getBucketType() {
        return bucketType;
    }

    public Optional<BucketTypeFilter> optionalBucketType() {
        return Optional.ofNullable(bucketType);
    }

    public BucketTypeFilter bucketType() {
        if (bucketType == null) {
            setBucketType(new BucketTypeFilter());
        }
        return bucketType;
    }

    public void setBucketType(BucketTypeFilter bucketType) {
        this.bucketType = bucketType;
    }

    public InstantFilter getBucketStart() {
        return bucketStart;
    }

    public Optional<InstantFilter> optionalBucketStart() {
        return Optional.ofNullable(bucketStart);
    }

    public InstantFilter bucketStart() {
        if (bucketStart == null) {
            setBucketStart(new InstantFilter());
        }
        return bucketStart;
    }

    public void setBucketStart(InstantFilter bucketStart) {
        this.bucketStart = bucketStart;
    }

    public InstantFilter getBucketEnd() {
        return bucketEnd;
    }

    public Optional<InstantFilter> optionalBucketEnd() {
        return Optional.ofNullable(bucketEnd);
    }

    public InstantFilter bucketEnd() {
        if (bucketEnd == null) {
            setBucketEnd(new InstantFilter());
        }
        return bucketEnd;
    }

    public void setBucketEnd(InstantFilter bucketEnd) {
        this.bucketEnd = bucketEnd;
    }

    public StringFilter getDimensionName() {
        return dimensionName;
    }

    public Optional<StringFilter> optionalDimensionName() {
        return Optional.ofNullable(dimensionName);
    }

    public StringFilter dimensionName() {
        if (dimensionName == null) {
            setDimensionName(new StringFilter());
        }
        return dimensionName;
    }

    public void setDimensionName(StringFilter dimensionName) {
        this.dimensionName = dimensionName;
    }

    public StringFilter getDimensionValue() {
        return dimensionValue;
    }

    public Optional<StringFilter> optionalDimensionValue() {
        return Optional.ofNullable(dimensionValue);
    }

    public StringFilter dimensionValue() {
        if (dimensionValue == null) {
            setDimensionValue(new StringFilter());
        }
        return dimensionValue;
    }

    public void setDimensionValue(StringFilter dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public LongFilter getValue() {
        return value;
    }

    public Optional<LongFilter> optionalValue() {
        return Optional.ofNullable(value);
    }

    public LongFilter value() {
        if (value == null) {
            setValue(new LongFilter());
        }
        return value;
    }

    public void setValue(LongFilter value) {
        this.value = value;
    }

    public InstantFilter getComputedAt() {
        return computedAt;
    }

    public Optional<InstantFilter> optionalComputedAt() {
        return Optional.ofNullable(computedAt);
    }

    public InstantFilter computedAt() {
        if (computedAt == null) {
            setComputedAt(new InstantFilter());
        }
        return computedAt;
    }

    public void setComputedAt(InstantFilter computedAt) {
        this.computedAt = computedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MetricRollupCriteria that = (MetricRollupCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(metricKey, that.metricKey) &&
            Objects.equals(bucketType, that.bucketType) &&
            Objects.equals(bucketStart, that.bucketStart) &&
            Objects.equals(bucketEnd, that.bucketEnd) &&
            Objects.equals(dimensionName, that.dimensionName) &&
            Objects.equals(dimensionValue, that.dimensionValue) &&
            Objects.equals(value, that.value) &&
            Objects.equals(computedAt, that.computedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, metricKey, bucketType, bucketStart, bucketEnd, dimensionName, dimensionValue, value, computedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricRollupCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMetricKey().map(f -> "metricKey=" + f + ", ").orElse("") +
            optionalBucketType().map(f -> "bucketType=" + f + ", ").orElse("") +
            optionalBucketStart().map(f -> "bucketStart=" + f + ", ").orElse("") +
            optionalBucketEnd().map(f -> "bucketEnd=" + f + ", ").orElse("") +
            optionalDimensionName().map(f -> "dimensionName=" + f + ", ").orElse("") +
            optionalDimensionValue().map(f -> "dimensionValue=" + f + ", ").orElse("") +
            optionalValue().map(f -> "value=" + f + ", ").orElse("") +
            optionalComputedAt().map(f -> "computedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
