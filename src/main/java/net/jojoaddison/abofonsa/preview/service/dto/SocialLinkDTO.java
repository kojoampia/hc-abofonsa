package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SocialPlatform;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.SocialLink} entity.
 */
@Schema(description = "Footer social icons and contact channels — one row per link, ordered and toggleable.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SocialLinkDTO implements Serializable {

    private Long id;

    @NotNull
    private SocialPlatform platform;

    @NotNull
    @Size(max = 80)
    private String label;

    @NotNull
    @Size(max = 512)
    private String url;

    @Size(max = 60)
    private String iconKey;

    @NotNull
    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SocialPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SocialPlatform platform) {
        this.platform = platform;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
        if (!(o instanceof SocialLinkDTO)) {
            return false;
        }

        SocialLinkDTO socialLinkDTO = (SocialLinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, socialLinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SocialLinkDTO{" +
            "id=" + getId() +
            ", platform='" + getPlatform() + "'" +
            ", label='" + getLabel() + "'" +
            ", url='" + getUrl() + "'" +
            ", iconKey='" + getIconKey() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
