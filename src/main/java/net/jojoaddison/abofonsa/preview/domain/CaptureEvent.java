package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The raw event log the metrics are derived from. Append-only: rollups are recomputed from
 * here, so this table is the single source of truth for every chart.
 *
 * `occurredDate` is redundant with `occurredAt` on purpose — it is the index the day/week/month
 * drill-downs group on, and grouping on a derived date_trunc cannot use an index on the Instant.
 */
@Entity
@Table(name = "capture_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CaptureEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private CaptureEventType eventType;

    @NotNull
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @NotNull
    @Column(name = "occurred_date", nullable = false)
    private LocalDate occurredDate;

    /**
     * Rotating per-visit hash; supports UNIQUE_VISITORS without a persistent identifier.
     */
    @Size(max = 64)
    @Column(name = "session_hash", length = 64)
    private String sessionHash;

    @Size(max = 10)
    @Column(name = "locale", length = 10)
    private String locale;

    @Size(max = 255)
    @Column(name = "source_page", length = 255)
    private String sourcePage;

    @Size(max = 120)
    @Column(name = "utm_source", length = 120)
    private String utmSource;

    @Size(max = 120)
    @Column(name = "utm_medium", length = 120)
    private String utmMedium;

    @Size(max = 120)
    @Column(name = "utm_campaign", length = 120)
    private String utmCampaign;

    @Size(max = 255)
    @Column(name = "referrer_host", length = 255)
    private String referrerHost;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Size(max = 2)
    @Column(name = "country_code", length = 2)
    private String countryCode;

    /**
     * What was interacted with — a service slug, plan code, tier code or social platform.
     */
    @Size(max = 120)
    @Column(name = "target_key", length = 120)
    private String targetKey;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CaptureEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaptureEventType getEventType() {
        return this.eventType;
    }

    public CaptureEvent eventType(CaptureEventType eventType) {
        this.setEventType(eventType);
        return this;
    }

    public void setEventType(CaptureEventType eventType) {
        this.eventType = eventType;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public CaptureEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDate getOccurredDate() {
        return this.occurredDate;
    }

    public CaptureEvent occurredDate(LocalDate occurredDate) {
        this.setOccurredDate(occurredDate);
        return this;
    }

    public void setOccurredDate(LocalDate occurredDate) {
        this.occurredDate = occurredDate;
    }

    public String getSessionHash() {
        return this.sessionHash;
    }

    public CaptureEvent sessionHash(String sessionHash) {
        this.setSessionHash(sessionHash);
        return this;
    }

    public void setSessionHash(String sessionHash) {
        this.sessionHash = sessionHash;
    }

    public String getLocale() {
        return this.locale;
    }

    public CaptureEvent locale(String locale) {
        this.setLocale(locale);
        return this;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSourcePage() {
        return this.sourcePage;
    }

    public CaptureEvent sourcePage(String sourcePage) {
        this.setSourcePage(sourcePage);
        return this;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public String getUtmSource() {
        return this.utmSource;
    }

    public CaptureEvent utmSource(String utmSource) {
        this.setUtmSource(utmSource);
        return this;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return this.utmMedium;
    }

    public CaptureEvent utmMedium(String utmMedium) {
        this.setUtmMedium(utmMedium);
        return this;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return this.utmCampaign;
    }

    public CaptureEvent utmCampaign(String utmCampaign) {
        this.setUtmCampaign(utmCampaign);
        return this;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getReferrerHost() {
        return this.referrerHost;
    }

    public CaptureEvent referrerHost(String referrerHost) {
        this.setReferrerHost(referrerHost);
        return this;
    }

    public void setReferrerHost(String referrerHost) {
        this.referrerHost = referrerHost;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public CaptureEvent deviceType(DeviceType deviceType) {
        this.setDeviceType(deviceType);
        return this;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public CaptureEvent countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTargetKey() {
        return this.targetKey;
    }

    public CaptureEvent targetKey(String targetKey) {
        this.setTargetKey(targetKey);
        return this;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaptureEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((CaptureEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CaptureEvent{" +
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
