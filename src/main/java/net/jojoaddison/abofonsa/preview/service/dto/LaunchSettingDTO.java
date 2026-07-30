package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.LaunchSetting} entity.
 */
@Schema(
    description = "Single-row site configuration, including the launch instant the countdown ticks down to.\n`settingKey` is fixed at 'DEFAULT' and carries the unique constraint, so a second row cannot\nbe created and leave the page choosing between two launch dates."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LaunchSettingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String settingKey;

    @NotNull
    @Size(max = 120)
    private String organisationName;

    @Size(max = 255)
    private String tagline;

    @NotNull
    private Instant launchAt;

    @NotNull
    @Size(max = 64)
    private String launchTimezone;

    @NotNull
    @Size(max = 512)
    private String fundUrl;

    @NotNull
    @Size(max = 254)
    private String contactEmail;

    @Size(max = 40)
    private String contactPhone;

    @Size(max = 255)
    private String officeAddress;

    @NotNull
    @Size(max = 120)
    private String parentCompanyName;

    @NotNull
    @Size(max = 255)
    private String parentCompanyUrl;

    @NotNull
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public Instant getLaunchAt() {
        return launchAt;
    }

    public void setLaunchAt(Instant launchAt) {
        this.launchAt = launchAt;
    }

    public String getLaunchTimezone() {
        return launchTimezone;
    }

    public void setLaunchTimezone(String launchTimezone) {
        this.launchTimezone = launchTimezone;
    }

    public String getFundUrl() {
        return fundUrl;
    }

    public void setFundUrl(String fundUrl) {
        this.fundUrl = fundUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getParentCompanyName() {
        return parentCompanyName;
    }

    public void setParentCompanyName(String parentCompanyName) {
        this.parentCompanyName = parentCompanyName;
    }

    public String getParentCompanyUrl() {
        return parentCompanyUrl;
    }

    public void setParentCompanyUrl(String parentCompanyUrl) {
        this.parentCompanyUrl = parentCompanyUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaunchSettingDTO)) {
            return false;
        }

        LaunchSettingDTO launchSettingDTO = (LaunchSettingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, launchSettingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaunchSettingDTO{" +
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
            ", parentCompanyName='" + getParentCompanyName() + "'" +
            ", parentCompanyUrl='" + getParentCompanyUrl() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
