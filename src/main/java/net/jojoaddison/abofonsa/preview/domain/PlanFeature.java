package net.jojoaddison.abofonsa.preview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PlanFeature.
 */
@Entity
@Table(name = "plan_feature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanFeature implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column(name = "label", length = 160, nullable = false)
    private String label;

    @NotNull
    @Column(name = "included", nullable = false)
    private Boolean included;

    @NotNull
    @Column(name = "emphasised", nullable = false)
    private Boolean emphasised;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "features" }, allowSetters = true)
    private CarePlanTeaser plan;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlanFeature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public PlanFeature label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getIncluded() {
        return this.included;
    }

    public PlanFeature included(Boolean included) {
        this.setIncluded(included);
        return this;
    }

    public void setIncluded(Boolean included) {
        this.included = included;
    }

    public Boolean getEmphasised() {
        return this.emphasised;
    }

    public PlanFeature emphasised(Boolean emphasised) {
        this.setEmphasised(emphasised);
        return this;
    }

    public void setEmphasised(Boolean emphasised) {
        this.emphasised = emphasised;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public PlanFeature displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public CarePlanTeaser getPlan() {
        return this.plan;
    }

    public void setPlan(CarePlanTeaser carePlanTeaser) {
        this.plan = carePlanTeaser;
    }

    public PlanFeature plan(CarePlanTeaser carePlanTeaser) {
        this.setPlan(carePlanTeaser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanFeature)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanFeature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanFeature{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", included='" + getIncluded() + "'" +
            ", emphasised='" + getEmphasised() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            "}";
    }
}
