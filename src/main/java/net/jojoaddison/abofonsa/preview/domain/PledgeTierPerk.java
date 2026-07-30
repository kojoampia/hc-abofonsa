package net.jojoaddison.abofonsa.preview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PledgeTierPerk.
 */
@Entity
@Table(name = "pledge_tier_perk")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PledgeTierPerk implements Serializable {

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
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "perks" }, allowSetters = true)
    private PledgeTierTeaser tier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PledgeTierPerk id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public PledgeTierPerk label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public PledgeTierPerk displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public PledgeTierTeaser getTier() {
        return this.tier;
    }

    public void setTier(PledgeTierTeaser pledgeTierTeaser) {
        this.tier = pledgeTierTeaser;
    }

    public PledgeTierPerk tier(PledgeTierTeaser pledgeTierTeaser) {
        this.setTier(pledgeTierTeaser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PledgeTierPerk)) {
            return false;
        }
        return getId() != null && getId().equals(((PledgeTierPerk) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PledgeTierPerk{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            "}";
    }
}
