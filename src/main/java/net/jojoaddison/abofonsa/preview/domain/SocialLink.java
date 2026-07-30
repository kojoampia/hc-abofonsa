package net.jojoaddison.abofonsa.preview.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import net.jojoaddison.abofonsa.preview.domain.enumeration.SocialPlatform;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Footer social icons and contact channels — one row per link, ordered and toggleable.
 */
@Entity
@Table(name = "social_link")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SocialLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private SocialPlatform platform;

    @NotNull
    @Size(max = 80)
    @Column(name = "label", length = 80, nullable = false)
    private String label;

    @NotNull
    @Size(max = 512)
    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Size(max = 60)
    @Column(name = "icon_key", length = 60)
    private String iconKey;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SocialLink id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SocialPlatform getPlatform() {
        return this.platform;
    }

    public SocialLink platform(SocialPlatform platform) {
        this.setPlatform(platform);
        return this;
    }

    public void setPlatform(SocialPlatform platform) {
        this.platform = platform;
    }

    public String getLabel() {
        return this.label;
    }

    public SocialLink label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return this.url;
    }

    public SocialLink url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconKey() {
        return this.iconKey;
    }

    public SocialLink iconKey(String iconKey) {
        this.setIconKey(iconKey);
        return this;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public SocialLink displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SocialLink active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SocialLink)) {
            return false;
        }
        return getId() != null && getId().equals(((SocialLink) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SocialLink{" +
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
