package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.LaunchMilestone} entity.
 */
@Schema(description = "A step on the 'Road to launch' timeline.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LaunchMilestoneDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String phaseLabel;

    @NotNull
    @Size(max = 120)
    private String title;

    @Lob
    private String body;

    private LocalDate milestoneDate;

    @NotNull
    private Boolean current;

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

    public String getPhaseLabel() {
        return phaseLabel;
    }

    public void setPhaseLabel(String phaseLabel) {
        this.phaseLabel = phaseLabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
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
        if (!(o instanceof LaunchMilestoneDTO)) {
            return false;
        }

        LaunchMilestoneDTO launchMilestoneDTO = (LaunchMilestoneDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, launchMilestoneDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaunchMilestoneDTO{" +
            "id=" + getId() +
            ", phaseLabel='" + getPhaseLabel() + "'" +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", milestoneDate='" + getMilestoneDate() + "'" +
            ", current='" + getCurrent() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
