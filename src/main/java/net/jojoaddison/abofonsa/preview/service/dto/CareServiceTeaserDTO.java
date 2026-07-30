package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser} entity.
 */
@Schema(description = "A care service teaser card. Seeded from hc-abofonsa-web's six services.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CareServiceTeaserDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String slug;

    @NotNull
    @Size(max = 120)
    private String name;

    @Lob
    private String blurb;

    @Size(max = 60)
    private String iconKey;

    @Size(max = 160)
    private String availableOn;

    @NotNull
    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private Boolean published;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public String getAvailableOn() {
        return availableOn;
    }

    public void setAvailableOn(String availableOn) {
        this.availableOn = availableOn;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CareServiceTeaserDTO)) {
            return false;
        }

        CareServiceTeaserDTO careServiceTeaserDTO = (CareServiceTeaserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, careServiceTeaserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CareServiceTeaserDTO{" +
            "id=" + getId() +
            ", slug='" + getSlug() + "'" +
            ", name='" + getName() + "'" +
            ", blurb='" + getBlurb() + "'" +
            ", iconKey='" + getIconKey() + "'" +
            ", availableOn='" + getAvailableOn() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
