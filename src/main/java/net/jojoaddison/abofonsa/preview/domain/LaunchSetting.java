package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Single-row site configuration, including the launch instant the countdown ticks down to.
 * `settingKey` is fixed at 'DEFAULT' and carries the unique constraint, so a second row cannot
 * be created and leave the page choosing between two launch dates.
 */
@Entity
@Table(name = "launch_setting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LaunchSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "setting_key", length = 80, nullable = false, unique = true)
    private String settingKey;

    @NotNull
    @Size(max = 120)
    @Column(name = "organisation_name", length = 120, nullable = false)
    private String organisationName;

    @Size(max = 255)
    @Column(name = "tagline", length = 255)
    private String tagline;

    @NotNull
    @Column(name = "launch_at", nullable = false)
    private Instant launchAt;

    @NotNull
    @Size(max = 64)
    @Column(name = "launch_timezone", length = 64, nullable = false)
    private String launchTimezone;

    @NotNull
    @Size(max = 512)
    @Column(name = "fund_url", length = 512, nullable = false)
    private String fundUrl;

    @NotNull
    @Size(max = 254)
    @Column(name = "contact_email", length = 254, nullable = false)
    private String contactEmail;

    @Size(max = 40)
    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Size(max = 255)
    @Column(name = "office_address", length = 255)
    private String officeAddress;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LaunchSetting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettingKey() {
        return this.settingKey;
    }

    public LaunchSetting settingKey(String settingKey) {
        this.setSettingKey(settingKey);
        return this;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getOrganisationName() {
        return this.organisationName;
    }

    public LaunchSetting organisationName(String organisationName) {
        this.setOrganisationName(organisationName);
        return this;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getTagline() {
        return this.tagline;
    }

    public LaunchSetting tagline(String tagline) {
        this.setTagline(tagline);
        return this;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public Instant getLaunchAt() {
        return this.launchAt;
    }

    public LaunchSetting launchAt(Instant launchAt) {
        this.setLaunchAt(launchAt);
        return this;
    }

    public void setLaunchAt(Instant launchAt) {
        this.launchAt = launchAt;
    }

    public String getLaunchTimezone() {
        return this.launchTimezone;
    }

    public LaunchSetting launchTimezone(String launchTimezone) {
        this.setLaunchTimezone(launchTimezone);
        return this;
    }

    public void setLaunchTimezone(String launchTimezone) {
        this.launchTimezone = launchTimezone;
    }

    public String getFundUrl() {
        return this.fundUrl;
    }

    public LaunchSetting fundUrl(String fundUrl) {
        this.setFundUrl(fundUrl);
        return this;
    }

    public void setFundUrl(String fundUrl) {
        this.fundUrl = fundUrl;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public LaunchSetting contactEmail(String contactEmail) {
        this.setContactEmail(contactEmail);
        return this;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public LaunchSetting contactPhone(String contactPhone) {
        this.setContactPhone(contactPhone);
        return this;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getOfficeAddress() {
        return this.officeAddress;
    }

    public LaunchSetting officeAddress(String officeAddress) {
        this.setOfficeAddress(officeAddress);
        return this;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Boolean getActive() {
        return this.active;
    }

    public LaunchSetting active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaunchSetting)) {
            return false;
        }
        return getId() != null && getId().equals(((LaunchSetting) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaunchSetting{" +
            "id=" + getId() +
            ", settingKey='" + getSettingKey() + "'" +
            ", organisationName='" + getOrganisationName() + "'" +
            ", tagline='" + getTagline() + "'" +
            ", launchAt='" + getLaunchAt() + "'" +
            ", launchTimezone='" + getLaunchTimezone() + "'" +
            ", fundUrl='" + getFundUrl() + "'" +
            ", contactEmail='" + getContactEmail() + "'" +
            ", contactPhone='" + getContactPhone() + "'" +
            ", officeAddress='" + getOfficeAddress() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
