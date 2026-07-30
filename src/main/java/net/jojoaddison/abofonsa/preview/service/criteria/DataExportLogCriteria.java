package net.jojoaddison.abofonsa.preview.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportFormat;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportKind;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link net.jojoaddison.abofonsa.preview.domain.DataExportLog} entity. This class is used
 * in {@link net.jojoaddison.abofonsa.preview.web.rest.DataExportLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /data-export-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataExportLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ExportKind
     */
    public static class ExportKindFilter extends Filter<ExportKind> {

        public ExportKindFilter() {}

        public ExportKindFilter(ExportKindFilter filter) {
            super(filter);
        }

        @Override
        public ExportKindFilter copy() {
            return new ExportKindFilter(this);
        }
    }

    /**
     * Class for filtering ExportFormat
     */
    public static class ExportFormatFilter extends Filter<ExportFormat> {

        public ExportFormatFilter() {}

        public ExportFormatFilter(ExportFormatFilter filter) {
            super(filter);
        }

        @Override
        public ExportFormatFilter copy() {
            return new ExportFormatFilter(this);
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

    private ExportKindFilter exportKind;

    private ExportFormatFilter format;

    private InstantFilter rangeFrom;

    private InstantFilter rangeTo;

    private BucketTypeFilter bucketType;

    private StringFilter filterSummary;

    private IntegerFilter rowCount;

    private StringFilter requestedBy;

    private InstantFilter requestedAt;

    private LongFilter durationMs;

    private Boolean distinct;

    public DataExportLogCriteria() {}

    public DataExportLogCriteria(DataExportLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.exportKind = other.optionalExportKind().map(ExportKindFilter::copy).orElse(null);
        this.format = other.optionalFormat().map(ExportFormatFilter::copy).orElse(null);
        this.rangeFrom = other.optionalRangeFrom().map(InstantFilter::copy).orElse(null);
        this.rangeTo = other.optionalRangeTo().map(InstantFilter::copy).orElse(null);
        this.bucketType = other.optionalBucketType().map(BucketTypeFilter::copy).orElse(null);
        this.filterSummary = other.optionalFilterSummary().map(StringFilter::copy).orElse(null);
        this.rowCount = other.optionalRowCount().map(IntegerFilter::copy).orElse(null);
        this.requestedBy = other.optionalRequestedBy().map(StringFilter::copy).orElse(null);
        this.requestedAt = other.optionalRequestedAt().map(InstantFilter::copy).orElse(null);
        this.durationMs = other.optionalDurationMs().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DataExportLogCriteria copy() {
        return new DataExportLogCriteria(this);
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

    public ExportKindFilter getExportKind() {
        return exportKind;
    }

    public Optional<ExportKindFilter> optionalExportKind() {
        return Optional.ofNullable(exportKind);
    }

    public ExportKindFilter exportKind() {
        if (exportKind == null) {
            setExportKind(new ExportKindFilter());
        }
        return exportKind;
    }

    public void setExportKind(ExportKindFilter exportKind) {
        this.exportKind = exportKind;
    }

    public ExportFormatFilter getFormat() {
        return format;
    }

    public Optional<ExportFormatFilter> optionalFormat() {
        return Optional.ofNullable(format);
    }

    public ExportFormatFilter format() {
        if (format == null) {
            setFormat(new ExportFormatFilter());
        }
        return format;
    }

    public void setFormat(ExportFormatFilter format) {
        this.format = format;
    }

    public InstantFilter getRangeFrom() {
        return rangeFrom;
    }

    public Optional<InstantFilter> optionalRangeFrom() {
        return Optional.ofNullable(rangeFrom);
    }

    public InstantFilter rangeFrom() {
        if (rangeFrom == null) {
            setRangeFrom(new InstantFilter());
        }
        return rangeFrom;
    }

    public void setRangeFrom(InstantFilter rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public InstantFilter getRangeTo() {
        return rangeTo;
    }

    public Optional<InstantFilter> optionalRangeTo() {
        return Optional.ofNullable(rangeTo);
    }

    public InstantFilter rangeTo() {
        if (rangeTo == null) {
            setRangeTo(new InstantFilter());
        }
        return rangeTo;
    }

    public void setRangeTo(InstantFilter rangeTo) {
        this.rangeTo = rangeTo;
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

    public StringFilter getFilterSummary() {
        return filterSummary;
    }

    public Optional<StringFilter> optionalFilterSummary() {
        return Optional.ofNullable(filterSummary);
    }

    public StringFilter filterSummary() {
        if (filterSummary == null) {
            setFilterSummary(new StringFilter());
        }
        return filterSummary;
    }

    public void setFilterSummary(StringFilter filterSummary) {
        this.filterSummary = filterSummary;
    }

    public IntegerFilter getRowCount() {
        return rowCount;
    }

    public Optional<IntegerFilter> optionalRowCount() {
        return Optional.ofNullable(rowCount);
    }

    public IntegerFilter rowCount() {
        if (rowCount == null) {
            setRowCount(new IntegerFilter());
        }
        return rowCount;
    }

    public void setRowCount(IntegerFilter rowCount) {
        this.rowCount = rowCount;
    }

    public StringFilter getRequestedBy() {
        return requestedBy;
    }

    public Optional<StringFilter> optionalRequestedBy() {
        return Optional.ofNullable(requestedBy);
    }

    public StringFilter requestedBy() {
        if (requestedBy == null) {
            setRequestedBy(new StringFilter());
        }
        return requestedBy;
    }

    public void setRequestedBy(StringFilter requestedBy) {
        this.requestedBy = requestedBy;
    }

    public InstantFilter getRequestedAt() {
        return requestedAt;
    }

    public Optional<InstantFilter> optionalRequestedAt() {
        return Optional.ofNullable(requestedAt);
    }

    public InstantFilter requestedAt() {
        if (requestedAt == null) {
            setRequestedAt(new InstantFilter());
        }
        return requestedAt;
    }

    public void setRequestedAt(InstantFilter requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LongFilter getDurationMs() {
        return durationMs;
    }

    public Optional<LongFilter> optionalDurationMs() {
        return Optional.ofNullable(durationMs);
    }

    public LongFilter durationMs() {
        if (durationMs == null) {
            setDurationMs(new LongFilter());
        }
        return durationMs;
    }

    public void setDurationMs(LongFilter durationMs) {
        this.durationMs = durationMs;
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
        final DataExportLogCriteria that = (DataExportLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(exportKind, that.exportKind) &&
            Objects.equals(format, that.format) &&
            Objects.equals(rangeFrom, that.rangeFrom) &&
            Objects.equals(rangeTo, that.rangeTo) &&
            Objects.equals(bucketType, that.bucketType) &&
            Objects.equals(filterSummary, that.filterSummary) &&
            Objects.equals(rowCount, that.rowCount) &&
            Objects.equals(requestedBy, that.requestedBy) &&
            Objects.equals(requestedAt, that.requestedAt) &&
            Objects.equals(durationMs, that.durationMs) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            exportKind,
            format,
            rangeFrom,
            rangeTo,
            bucketType,
            filterSummary,
            rowCount,
            requestedBy,
            requestedAt,
            durationMs,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataExportLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalExportKind().map(f -> "exportKind=" + f + ", ").orElse("") +
            optionalFormat().map(f -> "format=" + f + ", ").orElse("") +
            optionalRangeFrom().map(f -> "rangeFrom=" + f + ", ").orElse("") +
            optionalRangeTo().map(f -> "rangeTo=" + f + ", ").orElse("") +
            optionalBucketType().map(f -> "bucketType=" + f + ", ").orElse("") +
            optionalFilterSummary().map(f -> "filterSummary=" + f + ", ").orElse("") +
            optionalRowCount().map(f -> "rowCount=" + f + ", ").orElse("") +
            optionalRequestedBy().map(f -> "requestedBy=" + f + ", ").orElse("") +
            optionalRequestedAt().map(f -> "requestedAt=" + f + ", ").orElse("") +
            optionalDurationMs().map(f -> "durationMs=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
