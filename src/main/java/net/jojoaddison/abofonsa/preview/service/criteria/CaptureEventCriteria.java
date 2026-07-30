package net.jojoaddison.abofonsa.preview.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.enumeration.CaptureEventType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link net.jojoaddison.abofonsa.preview.domain.CaptureEvent} entity. This class is used
 * in {@link net.jojoaddison.abofonsa.preview.web.rest.CaptureEventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /capture-events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CaptureEventCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CaptureEventType
     */
    public static class CaptureEventTypeFilter extends Filter<CaptureEventType> {

        public CaptureEventTypeFilter() {}

        public CaptureEventTypeFilter(CaptureEventTypeFilter filter) {
            super(filter);
        }

        @Override
        public CaptureEventTypeFilter copy() {
            return new CaptureEventTypeFilter(this);
        }
    }

    /**
     * Class for filtering DeviceType
     */
    public static class DeviceTypeFilter extends Filter<DeviceType> {

        public DeviceTypeFilter() {}

        public DeviceTypeFilter(DeviceTypeFilter filter) {
            super(filter);
        }

        @Override
        public DeviceTypeFilter copy() {
            return new DeviceTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CaptureEventTypeFilter eventType;

    private InstantFilter occurredAt;

    private LocalDateFilter occurredDate;

    private StringFilter sessionHash;

    private StringFilter locale;

    private StringFilter sourcePage;

    private StringFilter utmSource;

    private StringFilter utmMedium;

    private StringFilter utmCampaign;

    private StringFilter referrerHost;

    private DeviceTypeFilter deviceType;

    private StringFilter countryCode;

    private StringFilter targetKey;

    private Boolean distinct;

    public CaptureEventCriteria() {}

    public CaptureEventCriteria(CaptureEventCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.eventType = other.optionalEventType().map(CaptureEventTypeFilter::copy).orElse(null);
        this.occurredAt = other.optionalOccurredAt().map(InstantFilter::copy).orElse(null);
        this.occurredDate = other.optionalOccurredDate().map(LocalDateFilter::copy).orElse(null);
        this.sessionHash = other.optionalSessionHash().map(StringFilter::copy).orElse(null);
        this.locale = other.optionalLocale().map(StringFilter::copy).orElse(null);
        this.sourcePage = other.optionalSourcePage().map(StringFilter::copy).orElse(null);
        this.utmSource = other.optionalUtmSource().map(StringFilter::copy).orElse(null);
        this.utmMedium = other.optionalUtmMedium().map(StringFilter::copy).orElse(null);
        this.utmCampaign = other.optionalUtmCampaign().map(StringFilter::copy).orElse(null);
        this.referrerHost = other.optionalReferrerHost().map(StringFilter::copy).orElse(null);
        this.deviceType = other.optionalDeviceType().map(DeviceTypeFilter::copy).orElse(null);
        this.countryCode = other.optionalCountryCode().map(StringFilter::copy).orElse(null);
        this.targetKey = other.optionalTargetKey().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CaptureEventCriteria copy() {
        return new CaptureEventCriteria(this);
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

    public CaptureEventTypeFilter getEventType() {
        return eventType;
    }

    public Optional<CaptureEventTypeFilter> optionalEventType() {
        return Optional.ofNullable(eventType);
    }

    public CaptureEventTypeFilter eventType() {
        if (eventType == null) {
            setEventType(new CaptureEventTypeFilter());
        }
        return eventType;
    }

    public void setEventType(CaptureEventTypeFilter eventType) {
        this.eventType = eventType;
    }

    public InstantFilter getOccurredAt() {
        return occurredAt;
    }

    public Optional<InstantFilter> optionalOccurredAt() {
        return Optional.ofNullable(occurredAt);
    }

    public InstantFilter occurredAt() {
        if (occurredAt == null) {
            setOccurredAt(new InstantFilter());
        }
        return occurredAt;
    }

    public void setOccurredAt(InstantFilter occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDateFilter getOccurredDate() {
        return occurredDate;
    }

    public Optional<LocalDateFilter> optionalOccurredDate() {
        return Optional.ofNullable(occurredDate);
    }

    public LocalDateFilter occurredDate() {
        if (occurredDate == null) {
            setOccurredDate(new LocalDateFilter());
        }
        return occurredDate;
    }

    public void setOccurredDate(LocalDateFilter occurredDate) {
        this.occurredDate = occurredDate;
    }

    public StringFilter getSessionHash() {
        return sessionHash;
    }

    public Optional<StringFilter> optionalSessionHash() {
        return Optional.ofNullable(sessionHash);
    }

    public StringFilter sessionHash() {
        if (sessionHash == null) {
            setSessionHash(new StringFilter());
        }
        return sessionHash;
    }

    public void setSessionHash(StringFilter sessionHash) {
        this.sessionHash = sessionHash;
    }

    public StringFilter getLocale() {
        return locale;
    }

    public Optional<StringFilter> optionalLocale() {
        return Optional.ofNullable(locale);
    }

    public StringFilter locale() {
        if (locale == null) {
            setLocale(new StringFilter());
        }
        return locale;
    }

    public void setLocale(StringFilter locale) {
        this.locale = locale;
    }

    public StringFilter getSourcePage() {
        return sourcePage;
    }

    public Optional<StringFilter> optionalSourcePage() {
        return Optional.ofNullable(sourcePage);
    }

    public StringFilter sourcePage() {
        if (sourcePage == null) {
            setSourcePage(new StringFilter());
        }
        return sourcePage;
    }

    public void setSourcePage(StringFilter sourcePage) {
        this.sourcePage = sourcePage;
    }

    public StringFilter getUtmSource() {
        return utmSource;
    }

    public Optional<StringFilter> optionalUtmSource() {
        return Optional.ofNullable(utmSource);
    }

    public StringFilter utmSource() {
        if (utmSource == null) {
            setUtmSource(new StringFilter());
        }
        return utmSource;
    }

    public void setUtmSource(StringFilter utmSource) {
        this.utmSource = utmSource;
    }

    public StringFilter getUtmMedium() {
        return utmMedium;
    }

    public Optional<StringFilter> optionalUtmMedium() {
        return Optional.ofNullable(utmMedium);
    }

    public StringFilter utmMedium() {
        if (utmMedium == null) {
            setUtmMedium(new StringFilter());
        }
        return utmMedium;
    }

    public void setUtmMedium(StringFilter utmMedium) {
        this.utmMedium = utmMedium;
    }

    public StringFilter getUtmCampaign() {
        return utmCampaign;
    }

    public Optional<StringFilter> optionalUtmCampaign() {
        return Optional.ofNullable(utmCampaign);
    }

    public StringFilter utmCampaign() {
        if (utmCampaign == null) {
            setUtmCampaign(new StringFilter());
        }
        return utmCampaign;
    }

    public void setUtmCampaign(StringFilter utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public StringFilter getReferrerHost() {
        return referrerHost;
    }

    public Optional<StringFilter> optionalReferrerHost() {
        return Optional.ofNullable(referrerHost);
    }

    public StringFilter referrerHost() {
        if (referrerHost == null) {
            setReferrerHost(new StringFilter());
        }
        return referrerHost;
    }

    public void setReferrerHost(StringFilter referrerHost) {
        this.referrerHost = referrerHost;
    }

    public DeviceTypeFilter getDeviceType() {
        return deviceType;
    }

    public Optional<DeviceTypeFilter> optionalDeviceType() {
        return Optional.ofNullable(deviceType);
    }

    public DeviceTypeFilter deviceType() {
        if (deviceType == null) {
            setDeviceType(new DeviceTypeFilter());
        }
        return deviceType;
    }

    public void setDeviceType(DeviceTypeFilter deviceType) {
        this.deviceType = deviceType;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public Optional<StringFilter> optionalCountryCode() {
        return Optional.ofNullable(countryCode);
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            setCountryCode(new StringFilter());
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getTargetKey() {
        return targetKey;
    }

    public Optional<StringFilter> optionalTargetKey() {
        return Optional.ofNullable(targetKey);
    }

    public StringFilter targetKey() {
        if (targetKey == null) {
            setTargetKey(new StringFilter());
        }
        return targetKey;
    }

    public void setTargetKey(StringFilter targetKey) {
        this.targetKey = targetKey;
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
        final CaptureEventCriteria that = (CaptureEventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(eventType, that.eventType) &&
            Objects.equals(occurredAt, that.occurredAt) &&
            Objects.equals(occurredDate, that.occurredDate) &&
            Objects.equals(sessionHash, that.sessionHash) &&
            Objects.equals(locale, that.locale) &&
            Objects.equals(sourcePage, that.sourcePage) &&
            Objects.equals(utmSource, that.utmSource) &&
            Objects.equals(utmMedium, that.utmMedium) &&
            Objects.equals(utmCampaign, that.utmCampaign) &&
            Objects.equals(referrerHost, that.referrerHost) &&
            Objects.equals(deviceType, that.deviceType) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(targetKey, that.targetKey) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            eventType,
            occurredAt,
            occurredDate,
            sessionHash,
            locale,
            sourcePage,
            utmSource,
            utmMedium,
            utmCampaign,
            referrerHost,
            deviceType,
            countryCode,
            targetKey,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CaptureEventCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEventType().map(f -> "eventType=" + f + ", ").orElse("") +
            optionalOccurredAt().map(f -> "occurredAt=" + f + ", ").orElse("") +
            optionalOccurredDate().map(f -> "occurredDate=" + f + ", ").orElse("") +
            optionalSessionHash().map(f -> "sessionHash=" + f + ", ").orElse("") +
            optionalLocale().map(f -> "locale=" + f + ", ").orElse("") +
            optionalSourcePage().map(f -> "sourcePage=" + f + ", ").orElse("") +
            optionalUtmSource().map(f -> "utmSource=" + f + ", ").orElse("") +
            optionalUtmMedium().map(f -> "utmMedium=" + f + ", ").orElse("") +
            optionalUtmCampaign().map(f -> "utmCampaign=" + f + ", ").orElse("") +
            optionalReferrerHost().map(f -> "referrerHost=" + f + ", ").orElse("") +
            optionalDeviceType().map(f -> "deviceType=" + f + ", ").orElse("") +
            optionalCountryCode().map(f -> "countryCode=" + f + ", ").orElse("") +
            optionalTargetKey().map(f -> "targetKey=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
