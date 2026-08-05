package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.AudienceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.DeviceType;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.WaitlistSignup} entity.
 */
@Schema(
    description = "One captured email. `emailNormalized` (lower-cased, trimmed) carries the unique constraint\nrather than `email`, so 'Ama@Clinic.org' and 'ama@clinic.org' cannot both get on the list\nwhile the address is still displayed back exactly as it was typed."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WaitlistSignupDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 254)
    @Pattern(regexp = "^[^@]+@[^@]+[.][^@]+$")
    private String email;

    @NotNull
    @Size(max = 254)
    private String emailNormalized;

    @Size(max = 120)
    private String fullName;

    @Size(max = 160)
    private String organisation;

    private AudienceType audience;

    private PlanCode planOfInterest;

    @NotNull
    private SignupStatus status;

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

    @Size(max = 512)
    private String referrer;

    private DeviceType deviceType;

    @NotNull
    private Boolean consentGiven;

    private Instant confirmedAt;

    private Instant unsubscribedAt;

    @NotNull
    private Instant capturedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailNormalized() {
        return emailNormalized;
    }

    public void setEmailNormalized(String emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public AudienceType getAudience() {
        return audience;
    }

    public void setAudience(AudienceType audience) {
        this.audience = audience;
    }

    public PlanCode getPlanOfInterest() {
        return planOfInterest;
    }

    public void setPlanOfInterest(PlanCode planOfInterest) {
        this.planOfInterest = planOfInterest;
    }

    public SignupStatus getStatus() {
        return status;
    }

    public void setStatus(SignupStatus status) {
        this.status = status;
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

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean getConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public void setUnsubscribedAt(Instant unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaitlistSignupDTO)) {
            return false;
        }

        WaitlistSignupDTO waitlistSignupDTO = (WaitlistSignupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, waitlistSignupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Identifies the row; does not reproduce it.
     *
     * <p>The generated version printed the address, the opt-in token and the salted IP hash, and
     * {@code WaitlistSignupResource} logs this object on every save. Production runs at INFO so it
     * never fired there, but the dev profile logs this package at DEBUG — which is how personal data
     * and a live credential end up in a terminal, a scrollback buffer and a pasted stack trace.
     */
    // prettier-ignore
    @Override
    public String toString() {
        return "WaitlistSignupDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", audience='" + getAudience() + "'" +
            ", planOfInterest='" + getPlanOfInterest() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            "}";
    }
}
