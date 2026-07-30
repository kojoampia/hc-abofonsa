package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import net.jojoaddison.abofonsa.preview.domain.enumeration.AudienceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * One captured email. `emailNormalized` (lower-cased, trimmed) carries the unique constraint
 * rather than `email`, so 'Ama@Clinic.org' and 'ama@clinic.org' cannot both get on the list
 * while the address is still displayed back exactly as it was typed.
 */
@Entity
@Table(name = "waitlist_signup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitlistSignup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 254)
    @Pattern(regexp = "^[^@]+@[^@]+[.][^@]+$")
    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @NotNull
    @Size(max = 254)
    @Column(name = "email_normalized", length = 254, nullable = false, unique = true)
    private String emailNormalized;

    @Size(max = 120)
    @Column(name = "full_name", length = 120)
    private String fullName;

    @Size(max = 160)
    @Column(name = "organisation", length = 160)
    private String organisation;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience")
    private AudienceType audience;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_of_interest")
    private PlanCode planOfInterest;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SignupStatus status;

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

    @Size(max = 512)
    @Column(name = "referrer", length = 512)
    private String referrer;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @NotNull
    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    @Size(max = 64)
    @Column(name = "confirmation_token", length = 64)
    private String confirmationToken;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "unsubscribed_at")
    private Instant unsubscribedAt;

    @NotNull
    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    /**
     * Salted hash, never the address itself — enough to rate-limit, not enough to re-identify.
     */
    @Size(max = 64)
    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Size(max = 512)
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WaitlistSignup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public WaitlistSignup email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailNormalized() {
        return this.emailNormalized;
    }

    public WaitlistSignup emailNormalized(String emailNormalized) {
        this.setEmailNormalized(emailNormalized);
        return this;
    }

    public void setEmailNormalized(String emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public String getFullName() {
        return this.fullName;
    }

    public WaitlistSignup fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganisation() {
        return this.organisation;
    }

    public WaitlistSignup organisation(String organisation) {
        this.setOrganisation(organisation);
        return this;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public AudienceType getAudience() {
        return this.audience;
    }

    public WaitlistSignup audience(AudienceType audience) {
        this.setAudience(audience);
        return this;
    }

    public void setAudience(AudienceType audience) {
        this.audience = audience;
    }

    public PlanCode getPlanOfInterest() {
        return this.planOfInterest;
    }

    public WaitlistSignup planOfInterest(PlanCode planOfInterest) {
        this.setPlanOfInterest(planOfInterest);
        return this;
    }

    public void setPlanOfInterest(PlanCode planOfInterest) {
        this.planOfInterest = planOfInterest;
    }

    public SignupStatus getStatus() {
        return this.status;
    }

    public WaitlistSignup status(SignupStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SignupStatus status) {
        this.status = status;
    }

    public String getLocale() {
        return this.locale;
    }

    public WaitlistSignup locale(String locale) {
        this.setLocale(locale);
        return this;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSourcePage() {
        return this.sourcePage;
    }

    public WaitlistSignup sourcePage(String sourcePage) {
        this.setSourcePage(sourcePage);
        return this;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public String getUtmSource() {
        return this.utmSource;
    }

    public WaitlistSignup utmSource(String utmSource) {
        this.setUtmSource(utmSource);
        return this;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return this.utmMedium;
    }

    public WaitlistSignup utmMedium(String utmMedium) {
        this.setUtmMedium(utmMedium);
        return this;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return this.utmCampaign;
    }

    public WaitlistSignup utmCampaign(String utmCampaign) {
        this.setUtmCampaign(utmCampaign);
        return this;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getReferrer() {
        return this.referrer;
    }

    public WaitlistSignup referrer(String referrer) {
        this.setReferrer(referrer);
        return this;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public WaitlistSignup deviceType(DeviceType deviceType) {
        this.setDeviceType(deviceType);
        return this;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean getConsentGiven() {
        return this.consentGiven;
    }

    public WaitlistSignup consentGiven(Boolean consentGiven) {
        this.setConsentGiven(consentGiven);
        return this;
    }

    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }

    public String getConfirmationToken() {
        return this.confirmationToken;
    }

    public WaitlistSignup confirmationToken(String confirmationToken) {
        this.setConfirmationToken(confirmationToken);
        return this;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Instant getConfirmedAt() {
        return this.confirmedAt;
    }

    public WaitlistSignup confirmedAt(Instant confirmedAt) {
        this.setConfirmedAt(confirmedAt);
        return this;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getUnsubscribedAt() {
        return this.unsubscribedAt;
    }

    public WaitlistSignup unsubscribedAt(Instant unsubscribedAt) {
        this.setUnsubscribedAt(unsubscribedAt);
        return this;
    }

    public void setUnsubscribedAt(Instant unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }

    public Instant getCapturedAt() {
        return this.capturedAt;
    }

    public WaitlistSignup capturedAt(Instant capturedAt) {
        this.setCapturedAt(capturedAt);
        return this;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public String getIpHash() {
        return this.ipHash;
    }

    public WaitlistSignup ipHash(String ipHash) {
        this.setIpHash(ipHash);
        return this;
    }

    public void setIpHash(String ipHash) {
        this.ipHash = ipHash;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public WaitlistSignup userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaitlistSignup)) {
            return false;
        }
        return getId() != null && getId().equals(((WaitlistSignup) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WaitlistSignup{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", emailNormalized='" + getEmailNormalized() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", organisation='" + getOrganisation() + "'" +
            ", audience='" + getAudience() + "'" +
            ", planOfInterest='" + getPlanOfInterest() + "'" +
            ", status='" + getStatus() + "'" +
            ", locale='" + getLocale() + "'" +
            ", sourcePage='" + getSourcePage() + "'" +
            ", utmSource='" + getUtmSource() + "'" +
            ", utmMedium='" + getUtmMedium() + "'" +
            ", utmCampaign='" + getUtmCampaign() + "'" +
            ", referrer='" + getReferrer() + "'" +
            ", deviceType='" + getDeviceType() + "'" +
            ", consentGiven='" + getConsentGiven() + "'" +
            ", confirmationToken='" + getConfirmationToken() + "'" +
            ", confirmedAt='" + getConfirmedAt() + "'" +
            ", unsubscribedAt='" + getUnsubscribedAt() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            ", ipHash='" + getIpHash() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            "}";
    }
}
