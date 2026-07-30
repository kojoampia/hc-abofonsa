package net.jojoaddison.abofonsa.preview.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.PlanFeature} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanFeatureDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String label;

    @NotNull
    private Boolean included;

    @NotNull
    private Boolean emphasised;

    @NotNull
    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private CarePlanTeaserDTO plan;

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

    public Boolean getIncluded() {
        return included;
    }

    public void setIncluded(Boolean included) {
        this.included = included;
    }

    public Boolean getEmphasised() {
        return emphasised;
    }

    public void setEmphasised(Boolean emphasised) {
        this.emphasised = emphasised;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public CarePlanTeaserDTO getPlan() {
        return plan;
    }

    public void setPlan(CarePlanTeaserDTO plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanFeatureDTO)) {
            return false;
        }

        PlanFeatureDTO planFeatureDTO = (PlanFeatureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planFeatureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanFeatureDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", included='" + getIncluded() + "'" +
            ", emphasised='" + getEmphasised() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", plan=" + getPlan() +
            "}";
    }
}
