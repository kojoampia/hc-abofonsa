package net.jojoaddison.abofonsa.preview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A care service teaser card. Seeded from hc-abofonsa-web's six services.
 */
@Entity
@Table(name = "care_service_teaser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CareServiceTeaser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "slug", length = 80, nullable = false, unique = true)
    private String slug;

    @NotNull
    @Size(max = 120)
    @Column(name = "name", length = 120, nullable = false)
    private String name;

    /*
     * @JdbcTypeCode(LONGVARCHAR) is required next to @Lob on PostgreSQL and is not optional styling.
     * Hibernate maps a bare @Lob String to a large-object `oid` and reads it with getLong(), while
     * Liquibase created this column as `text` — so without it every read fails at runtime with
     * "Bad value for type long". JHipster does not add it; re-generating this entity will drop it
     * again.
     */
    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "blurb", nullable = false)
    private String blurb;

    @Size(max = 60)
    @Column(name = "icon_key", length = 60)
    private String iconKey;

    @Size(max = 160)
    @Column(name = "available_on", length = 160)
    private String availableOn;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull
    @Column(name = "published", nullable = false)
    private Boolean published;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "service" }, allowSetters = true)
    private Set<ServiceHighlight> highlights = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CareServiceTeaser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return this.slug;
    }

    public CareServiceTeaser slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return this.name;
    }

    public CareServiceTeaser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlurb() {
        return this.blurb;
    }

    public CareServiceTeaser blurb(String blurb) {
        this.setBlurb(blurb);
        return this;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getIconKey() {
        return this.iconKey;
    }

    public CareServiceTeaser iconKey(String iconKey) {
        this.setIconKey(iconKey);
        return this;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public String getAvailableOn() {
        return this.availableOn;
    }

    public CareServiceTeaser availableOn(String availableOn) {
        this.setAvailableOn(availableOn);
        return this;
    }

    public void setAvailableOn(String availableOn) {
        this.availableOn = availableOn;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public CareServiceTeaser displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public CareServiceTeaser published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Set<ServiceHighlight> getHighlights() {
        return this.highlights;
    }

    public void setHighlights(Set<ServiceHighlight> serviceHighlights) {
        if (this.highlights != null) {
            this.highlights.forEach(i -> i.setService(null));
        }
        if (serviceHighlights != null) {
            serviceHighlights.forEach(i -> i.setService(this));
        }
        this.highlights = serviceHighlights;
    }

    public CareServiceTeaser highlights(Set<ServiceHighlight> serviceHighlights) {
        this.setHighlights(serviceHighlights);
        return this;
    }

    public CareServiceTeaser addHighlight(ServiceHighlight serviceHighlight) {
        this.highlights.add(serviceHighlight);
        serviceHighlight.setService(this);
        return this;
    }

    public CareServiceTeaser removeHighlight(ServiceHighlight serviceHighlight) {
        this.highlights.remove(serviceHighlight);
        serviceHighlight.setService(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CareServiceTeaser)) {
            return false;
        }
        return getId() != null && getId().equals(((CareServiceTeaser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CareServiceTeaser{" +
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
