package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.BucketType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportFormat;
import net.jojoaddison.abofonsa.preview.domain.enumeration.ExportKind;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.DataExportLog} entity.
 */
@Schema(description = "Audit trail for every drill-down export — who pulled which emails, when, and how many.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataExportLogDTO implements Serializable {

    private Long id;

    @NotNull
    private ExportKind exportKind;

    @NotNull
    private ExportFormat format;

    private Instant rangeFrom;

    private Instant rangeTo;

    private BucketType bucketType;

    @Size(max = 512)
    private String filterSummary;

    @NotNull
    @Min(value = 0)
    private Integer rowCount;

    @NotNull
    @Size(max = 120)
    private String requestedBy;

    @NotNull
    private Instant requestedAt;

    private Long durationMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExportKind getExportKind() {
        return exportKind;
    }

    public void setExportKind(ExportKind exportKind) {
        this.exportKind = exportKind;
    }

    public ExportFormat getFormat() {
        return format;
    }

    public void setFormat(ExportFormat format) {
        this.format = format;
    }

    public Instant getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(Instant rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public Instant getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(Instant rangeTo) {
        this.rangeTo = rangeTo;
    }

    public BucketType getBucketType() {
        return bucketType;
    }

    public void setBucketType(BucketType bucketType) {
        this.bucketType = bucketType;
    }

    public String getFilterSummary() {
        return filterSummary;
    }

    public void setFilterSummary(String filterSummary) {
        this.filterSummary = filterSummary;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataExportLogDTO)) {
            return false;
        }

        DataExportLogDTO dataExportLogDTO = (DataExportLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dataExportLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataExportLogDTO{" +
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
