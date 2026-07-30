package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A step on the 'Road to launch' timeline.
 */
@Entity
@Table(name = "launch_milestone")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LaunchMilestone implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 40)
    @Column(name = "phase_label", length = 40, nullable = false)
    private String phaseLabel;

    @NotNull
    @Size(max = 120)
    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "milestone_date")
    private LocalDate milestoneDate;

    @NotNull
    @Column(name = "current", nullable = false)
    private Boolean current;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull
    @Column(name = "published", nullable = false)
    private Boolean published;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LaunchMilestone id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhaseLabel() {
        return this.phaseLabel;
    }

    public LaunchMilestone phaseLabel(String phaseLabel) {
        this.setPhaseLabel(phaseLabel);
        return this;
    }

    public void setPhaseLabel(String phaseLabel) {
        this.phaseLabel = phaseLabel;
    }

    public String getTitle() {
        return this.title;
    }

    public LaunchMilestone title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public LaunchMilestone body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getMilestoneDate() {
        return this.milestoneDate;
    }

    public LaunchMilestone milestoneDate(LocalDate milestoneDate) {
        this.setMilestoneDate(milestoneDate);
        return this;
    }

    public void setMilestoneDate(LocalDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    public Boolean getCurrent() {
        return this.current;
    }

    public LaunchMilestone current(Boolean current) {
        this.setCurrent(current);
        return this;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public LaunchMilestone displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public LaunchMilestone published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaunchMilestone)) {
            return false;
        }
        return getId() != null && getId().equals(((LaunchMilestone) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaunchMilestone{" +
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
