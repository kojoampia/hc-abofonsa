package net.jojoaddison.abofonsa.preview.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.enumeration.AudienceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link net.jojoaddison.abofonsa.preview.domain.WaitlistSignup} entity. This class is used
 * in {@link net.jojoaddison.abofonsa.preview.web.rest.WaitlistSignupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /waitlist-signups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitlistSignupCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AudienceType
     */
    public static class AudienceTypeFilter extends Filter<AudienceType> {

        public AudienceTypeFilter() {}

        public AudienceTypeFilter(AudienceTypeFilter filter) {
            super(filter);
        }

        @Override
        public AudienceTypeFilter copy() {
            return new AudienceTypeFilter(this);
        }
    }

    /**
     * Class for filtering PlanCode
     */
    public static class PlanCodeFilter extends Filter<PlanCode> {

        public PlanCodeFilter() {}

        public PlanCodeFilter(PlanCodeFilter filter) {
            super(filter);
        }

        @Override
        public PlanCodeFilter copy() {
            return new PlanCodeFilter(this);
        }
    }

    /**
     * Class for filtering SignupStatus
     */
    public static class SignupStatusFilter extends Filter<SignupStatus> {

        public SignupStatusFilter() {}

        public SignupStatusFilter(SignupStatusFilter filter) {
            super(filter);
        }

        @Override
        public SignupStatusFilter copy() {
            return new SignupStatusFilter(this);
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

    private StringFilter email;

    private StringFilter emailNormalized;

    private StringFilter fullName;

    private StringFilter organisation;

    private AudienceTypeFilter audience;

    private PlanCodeFilter planOfInterest;

    private SignupStatusFilter status;

    private StringFilter locale;

    private StringFilter sourcePage;

    private StringFilter utmSource;

    private StringFilter utmMedium;

    private StringFilter utmCampaign;

    private StringFilter referrer;

    private DeviceTypeFilter deviceType;

    private BooleanFilter consentGiven;

    private InstantFilter confirmedAt;

    private InstantFilter unsubscribedAt;

    private InstantFilter capturedAt;

    private Boolean distinct;

    public WaitlistSignupCriteria() {}

    public WaitlistSignupCriteria(WaitlistSignupCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.emailNormalized = other.optionalEmailNormalized().map(StringFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.organisation = other.optionalOrganisation().map(StringFilter::copy).orElse(null);
        this.audience = other.optionalAudience().map(AudienceTypeFilter::copy).orElse(null);
        this.planOfInterest = other.optionalPlanOfInterest().map(PlanCodeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SignupStatusFilter::copy).orElse(null);
        this.locale = other.optionalLocale().map(StringFilter::copy).orElse(null);
        this.sourcePage = other.optionalSourcePage().map(StringFilter::copy).orElse(null);
        this.utmSource = other.optionalUtmSource().map(StringFilter::copy).orElse(null);
        this.utmMedium = other.optionalUtmMedium().map(StringFilter::copy).orElse(null);
        this.utmCampaign = other.optionalUtmCampaign().map(StringFilter::copy).orElse(null);
        this.referrer = other.optionalReferrer().map(StringFilter::copy).orElse(null);
        this.deviceType = other.optionalDeviceType().map(DeviceTypeFilter::copy).orElse(null);
        this.consentGiven = other.optionalConsentGiven().map(BooleanFilter::copy).orElse(null);
        this.confirmedAt = other.optionalConfirmedAt().map(InstantFilter::copy).orElse(null);
        this.unsubscribedAt = other.optionalUnsubscribedAt().map(InstantFilter::copy).orElse(null);
        this.capturedAt = other.optionalCapturedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WaitlistSignupCriteria copy() {
        return new WaitlistSignupCriteria(this);
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

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getEmailNormalized() {
        return emailNormalized;
    }

    public Optional<StringFilter> optionalEmailNormalized() {
        return Optional.ofNullable(emailNormalized);
    }

    public StringFilter emailNormalized() {
        if (emailNormalized == null) {
            setEmailNormalized(new StringFilter());
        }
        return emailNormalized;
    }

    public void setEmailNormalized(StringFilter emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public StringFilter getFullName() {
        return fullName;
    }

    public Optional<StringFilter> optionalFullName() {
        return Optional.ofNullable(fullName);
    }

    public StringFilter fullName() {
        if (fullName == null) {
            setFullName(new StringFilter());
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getOrganisation() {
        return organisation;
    }

    public Optional<StringFilter> optionalOrganisation() {
        return Optional.ofNullable(organisation);
    }

    public StringFilter organisation() {
        if (organisation == null) {
            setOrganisation(new StringFilter());
        }
        return organisation;
    }

    public void setOrganisation(StringFilter organisation) {
        this.organisation = organisation;
    }

    public AudienceTypeFilter getAudience() {
        return audience;
    }

    public Optional<AudienceTypeFilter> optionalAudience() {
        return Optional.ofNullable(audience);
    }

    public AudienceTypeFilter audience() {
        if (audience == null) {
            setAudience(new AudienceTypeFilter());
        }
        return audience;
    }

    public void setAudience(AudienceTypeFilter audience) {
        this.audience = audience;
    }

    public PlanCodeFilter getPlanOfInterest() {
        return planOfInterest;
    }

    public Optional<PlanCodeFilter> optionalPlanOfInterest() {
        return Optional.ofNullable(planOfInterest);
    }

    public PlanCodeFilter planOfInterest() {
        if (planOfInterest == null) {
            setPlanOfInterest(new PlanCodeFilter());
        }
        return planOfInterest;
    }

    public void setPlanOfInterest(PlanCodeFilter planOfInterest) {
        this.planOfInterest = planOfInterest;
    }

    public SignupStatusFilter getStatus() {
        return status;
    }

    public Optional<SignupStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SignupStatusFilter status() {
        if (status == null) {
            setStatus(new SignupStatusFilter());
        }
        return status;
    }

    public void setStatus(SignupStatusFilter status) {
        this.status = status;
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

    public StringFilter getReferrer() {
        return referrer;
    }

    public Optional<StringFilter> optionalReferrer() {
        return Optional.ofNullable(referrer);
    }

    public StringFilter referrer() {
        if (referrer == null) {
            setReferrer(new StringFilter());
        }
        return referrer;
    }

    public void setReferrer(StringFilter referrer) {
        this.referrer = referrer;
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

    public BooleanFilter getConsentGiven() {
        return consentGiven;
    }

    public Optional<BooleanFilter> optionalConsentGiven() {
        return Optional.ofNullable(consentGiven);
    }

    public BooleanFilter consentGiven() {
        if (consentGiven == null) {
            setConsentGiven(new BooleanFilter());
        }
        return consentGiven;
    }

    public void setConsentGiven(BooleanFilter consentGiven) {
        this.consentGiven = consentGiven;
    }

    public InstantFilter getConfirmedAt() {
        return confirmedAt;
    }

    public Optional<InstantFilter> optionalConfirmedAt() {
        return Optional.ofNullable(confirmedAt);
    }

    public InstantFilter confirmedAt() {
        if (confirmedAt == null) {
            setConfirmedAt(new InstantFilter());
        }
        return confirmedAt;
    }

    public void setConfirmedAt(InstantFilter confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public InstantFilter getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public Optional<InstantFilter> optionalUnsubscribedAt() {
        return Optional.ofNullable(unsubscribedAt);
    }

    public InstantFilter unsubscribedAt() {
        if (unsubscribedAt == null) {
            setUnsubscribedAt(new InstantFilter());
        }
        return unsubscribedAt;
    }

    public void setUnsubscribedAt(InstantFilter unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }

    public InstantFilter getCapturedAt() {
        return capturedAt;
    }

    public Optional<InstantFilter> optionalCapturedAt() {
        return Optional.ofNullable(capturedAt);
    }

    public InstantFilter capturedAt() {
        if (capturedAt == null) {
            setCapturedAt(new InstantFilter());
        }
        return capturedAt;
    }

    public void setCapturedAt(InstantFilter capturedAt) {
        this.capturedAt = capturedAt;
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
        final WaitlistSignupCriteria that = (WaitlistSignupCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(email, that.email) &&
            Objects.equals(emailNormalized, that.emailNormalized) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(organisation, that.organisation) &&
            Objects.equals(audience, that.audience) &&
            Objects.equals(planOfInterest, that.planOfInterest) &&
            Objects.equals(status, that.status) &&
            Objects.equals(locale, that.locale) &&
            Objects.equals(sourcePage, that.sourcePage) &&
            Objects.equals(utmSource, that.utmSource) &&
            Objects.equals(utmMedium, that.utmMedium) &&
            Objects.equals(utmCampaign, that.utmCampaign) &&
            Objects.equals(referrer, that.referrer) &&
            Objects.equals(deviceType, that.deviceType) &&
            Objects.equals(consentGiven, that.consentGiven) &&
            Objects.equals(confirmedAt, that.confirmedAt) &&
            Objects.equals(unsubscribedAt, that.unsubscribedAt) &&
            Objects.equals(capturedAt, that.capturedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            email,
            emailNormalized,
            fullName,
            organisation,
            audience,
            planOfInterest,
            status,
            locale,
            sourcePage,
            utmSource,
            utmMedium,
            utmCampaign,
            referrer,
            deviceType,
            consentGiven,
            confirmedAt,
            unsubscribedAt,
            capturedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WaitlistSignupCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalEmailNormalized().map(f -> "emailNormalized=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalOrganisation().map(f -> "organisation=" + f + ", ").orElse("") +
            optionalAudience().map(f -> "audience=" + f + ", ").orElse("") +
            optionalPlanOfInterest().map(f -> "planOfInterest=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLocale().map(f -> "locale=" + f + ", ").orElse("") +
            optionalSourcePage().map(f -> "sourcePage=" + f + ", ").orElse("") +
            optionalUtmSource().map(f -> "utmSource=" + f + ", ").orElse("") +
            optionalUtmMedium().map(f -> "utmMedium=" + f + ", ").orElse("") +
            optionalUtmCampaign().map(f -> "utmCampaign=" + f + ", ").orElse("") +
            optionalReferrer().map(f -> "referrer=" + f + ", ").orElse("") +
            optionalDeviceType().map(f -> "deviceType=" + f + ", ").orElse("") +
            optionalConsentGiven().map(f -> "consentGiven=" + f + ", ").orElse("") +
            optionalConfirmedAt().map(f -> "confirmedAt=" + f + ", ").orElse("") +
            optionalUnsubscribedAt().map(f -> "unsubscribedAt=" + f + ", ").orElse("") +
            optionalCapturedAt().map(f -> "capturedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
