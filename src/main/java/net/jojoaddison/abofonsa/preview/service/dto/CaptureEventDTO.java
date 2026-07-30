package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.CaptureEvent} entity.
 */
@Schema(
    description = "The raw event log the metrics are derived from. Append-only: rollups are recomputed from\nhere, so this table is the single source of truth for every chart.\n\n`occurredDate` is redundant with `occurredAt` on purpose — it is the index the day/week/month\ndrill-downs group on, and grouping on a derived date_trunc cannot use an index on the Instant."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CaptureEventDTO implements Serializable {

    private Long id;

    @NotNull
    private CaptureEventType eventType;

    @NotNull
    private Instant occurredAt;

    @NotNull
    private LocalDate occurredDate;

    @Size(max = 64)
    @Schema(description = "Rotating per-visit hash; supports UNIQUE_VISITORS without a persistent identifier.")
    private String sessionHash;

    @Size(max = 10)
    private String locale;

    @Size(max = 255)
    private String sourcePage;

    @Size(max = 120)
    private String utmSource;

    @Size(max = 120)
    private String utmMedium;

    @Size(max = 120)
    private String utmCampaign;

    @Size(max = 255)
    private String referrerHost;

    private DeviceType deviceType;

    @Size(max = 2)
    private String countryCode;

    @Size(max = 120)
    @Schema(description = "What was interacted with — a service slug, plan code, tier code or social platform.")
    private String targetKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaptureEventType getEventType() {
        return eventType;
    }

    public void setEventType(CaptureEventType eventType) {
        this.eventType = eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDate getOccurredDate() {
        return occurredDate;
    }

    public void setOccurredDate(LocalDate occurredDate) {
        this.occurredDate = occurredDate;
    }

    public String getSessionHash() {
        return sessionHash;
    }

    public void setSessionHash(String sessionHash) {
        this.sessionHash = sessionHash;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getReferrerHost() {
        return referrerHost;
    }

    public void setReferrerHost(String referrerHost) {
        this.referrerHost = referrerHost;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaptureEventDTO)) {
            return false;
        }

        CaptureEventDTO captureEventDTO = (CaptureEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, captureEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CaptureEventDTO{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", occurredDate='" + getOccurredDate() + "'" +
            ", sessionHash='" + getSessionHash() + "'" +
            ", locale='" + getLocale() + "'" +
            ", sourcePage='" + getSourcePage() + "'" +
            ", utmSource='" + getUtmSource() + "'" +
            ", utmMedium='" + getUtmMedium() + "'" +
            ", utmCampaign='" + getUtmCampaign() + "'" +
            ", referrerHost='" + getReferrerHost() + "'" +
            ", deviceType='" + getDeviceType() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", targetKey='" + getTargetKey() + "'" +
            "}";
    }
}
