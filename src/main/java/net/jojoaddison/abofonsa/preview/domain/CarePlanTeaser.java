package net.jojoaddison.abofonsa.preview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * PEAR / PAWPAW / MELON, with the same GHS pricing the main site quotes.
 */
@Entity
@Table(name = "care_plan_teaser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarePlanTeaser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true)
    private PlanCode code;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Lob
    @Column(name = "for_who", nullable = false)
    private String forWho;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal priceAmount;

    @NotNull
    @Size(max = 3)
    @Column(name = "price_currency", length = 3, nullable = false)
    private String priceCurrency;

    @NotNull
    @Size(max = 20)
    @Column(name = "price_period", length = 20, nullable = false)
    private String pricePeriod;

    @Size(max = 255)
    @Column(name = "price_note", length = 255)
    private String priceNote;

    @NotNull
    @Column(name = "featured", nullable = false)
    private Boolean featured;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull
    @Column(name = "published", nullable = false)
    private Boolean published;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plan")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "plan" }, allowSetters = true)
    private Set<PlanFeature> features = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CarePlanTeaser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanCode getCode() {
        return this.code;
    }

    public CarePlanTeaser code(PlanCode code) {
        this.setCode(code);
        return this;
    }

    public void setCode(PlanCode code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public CarePlanTeaser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForWho() {
        return this.forWho;
    }

    public CarePlanTeaser forWho(String forWho) {
        this.setForWho(forWho);
        return this;
    }

    public void setForWho(String forWho) {
        this.forWho = forWho;
    }

    public BigDecimal getPriceAmount() {
        return this.priceAmount;
    }

    public CarePlanTeaser priceAmount(BigDecimal priceAmount) {
        this.setPriceAmount(priceAmount);
        return this;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return this.priceCurrency;
    }

    public CarePlanTeaser priceCurrency(String priceCurrency) {
        this.setPriceCurrency(priceCurrency);
        return this;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public String getPricePeriod() {
        return this.pricePeriod;
    }

    public CarePlanTeaser pricePeriod(String pricePeriod) {
        this.setPricePeriod(pricePeriod);
        return this;
    }

    public void setPricePeriod(String pricePeriod) {
        this.pricePeriod = pricePeriod;
    }

    public String getPriceNote() {
        return this.priceNote;
    }

    public CarePlanTeaser priceNote(String priceNote) {
        this.setPriceNote(priceNote);
        return this;
    }

    public void setPriceNote(String priceNote) {
        this.priceNote = priceNote;
    }

    public Boolean getFeatured() {
        return this.featured;
    }

    public CarePlanTeaser featured(Boolean featured) {
        this.setFeatured(featured);
        return this;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public CarePlanTeaser displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public CarePlanTeaser published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Set<PlanFeature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<PlanFeature> planFeatures) {
        if (this.features != null) {
            this.features.forEach(i -> i.setPlan(null));
        }
        if (planFeatures != null) {
            planFeatures.forEach(i -> i.setPlan(this));
        }
        this.features = planFeatures;
    }

    public CarePlanTeaser features(Set<PlanFeature> planFeatures) {
        this.setFeatures(planFeatures);
        return this;
    }

    public CarePlanTeaser addFeature(PlanFeature planFeature) {
        this.features.add(planFeature);
        planFeature.setPlan(this);
        return this;
    }

    public CarePlanTeaser removeFeature(PlanFeature planFeature) {
        this.features.remove(planFeature);
        planFeature.setPlan(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarePlanTeaser)) {
            return false;
        }
        return getId() != null && getId().equals(((CarePlanTeaser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarePlanTeaser{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", forWho='" + getForWho() + "'" +
            ", priceAmount=" + getPriceAmount() +
            ", priceCurrency='" + getPriceCurrency() + "'" +
            ", pricePeriod='" + getPricePeriod() + "'" +
            ", priceNote='" + getPriceNote() + "'" +
            ", featured='" + getFeatured() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
