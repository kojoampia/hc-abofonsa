package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportFormat;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportKind;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Audit trail for every drill-down export — who pulled which emails, when, and how many.
 */
@Entity
@Table(name = "data_export_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataExportLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "export_kind", nullable = false)
    private ExportKind exportKind;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private ExportFormat format;

    @Column(name = "range_from")
    private Instant rangeFrom;

    @Column(name = "range_to")
    private Instant rangeTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_type")
    private BucketType bucketType;

    @Size(max = 512)
    @Column(name = "filter_summary", length = 512)
    private String filterSummary;

    @NotNull
    @Min(value = 0)
    @Column(name = "row_count", nullable = false)
    private Integer rowCount;

    @NotNull
    @Size(max = 120)
    @Column(name = "requested_by", length = 120, nullable = false)
    private String requestedBy;

    @NotNull
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DataExportLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExportKind getExportKind() {
        return this.exportKind;
    }

    public DataExportLog exportKind(ExportKind exportKind) {
        this.setExportKind(exportKind);
        return this;
    }

    public void setExportKind(ExportKind exportKind) {
        this.exportKind = exportKind;
    }

    public ExportFormat getFormat() {
        return this.format;
    }

    public DataExportLog format(ExportFormat format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(ExportFormat format) {
        this.format = format;
    }

    public Instant getRangeFrom() {
        return this.rangeFrom;
    }

    public DataExportLog rangeFrom(Instant rangeFrom) {
        this.setRangeFrom(rangeFrom);
        return this;
    }

    public void setRangeFrom(Instant rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public Instant getRangeTo() {
        return this.rangeTo;
    }

    public DataExportLog rangeTo(Instant rangeTo) {
        this.setRangeTo(rangeTo);
        return this;
    }

    public void setRangeTo(Instant rangeTo) {
        this.rangeTo = rangeTo;
    }

    public BucketType getBucketType() {
        return this.bucketType;
    }

    public DataExportLog bucketType(BucketType bucketType) {
        this.setBucketType(bucketType);
        return this;
    }

    public void setBucketType(BucketType bucketType) {
        this.bucketType = bucketType;
    }

    public String getFilterSummary() {
        return this.filterSummary;
    }

    public DataExportLog filterSummary(String filterSummary) {
        this.setFilterSummary(filterSummary);
        return this;
    }

    public void setFilterSummary(String filterSummary) {
        this.filterSummary = filterSummary;
    }

    public Integer getRowCount() {
        return this.rowCount;
    }

    public DataExportLog rowCount(Integer rowCount) {
        this.setRowCount(rowCount);
        return this;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public String getRequestedBy() {
        return this.requestedBy;
    }

    public DataExportLog requestedBy(String requestedBy) {
        this.setRequestedBy(requestedBy);
        return this;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return this.requestedAt;
    }

    public DataExportLog requestedAt(Instant requestedAt) {
        this.setRequestedAt(requestedAt);
        return this;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Long getDurationMs() {
        return this.durationMs;
    }

    public DataExportLog durationMs(Long durationMs) {
        this.setDurationMs(durationMs);
        return this;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataExportLog)) {
            return false;
        }
        return getId() != null && getId().equals(((DataExportLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataExportLog{" +
            "id=" + getId() +
            ", exportKind='" + getExportKind() + "'" +
            ", format='" + getFormat() + "'" +
            ", rangeFrom='" + getRangeFrom() + "'" +
            ", rangeTo='" + getRangeTo() + "'" +
            ", bucketType='" + getBucketType() + "'" +
            ", filterSummary='" + getFilterSummary() + "'" +
            ", rowCount=" + getRowCount() +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", durationMs=" + getDurationMs() +
            "}";
    }
}
