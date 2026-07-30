package net.jojoaddison.abofonsa.preview.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PledgeTierPerkDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String label;

    @NotNull
    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private PledgeTierTeaserDTO tier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public PledgeTierTeaserDTO getTier() {
        return tier;
    }

    public void setTier(PledgeTierTeaserDTO tier) {
        this.tier = tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PledgeTierPerkDTO)) {
            return false;
        }

        PledgeTierPerkDTO pledgeTierPerkDTO = (PledgeTierPerkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pledgeTierPerkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PledgeTierPerkDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", tier=" + getTier() +
            "}";
    }
}
