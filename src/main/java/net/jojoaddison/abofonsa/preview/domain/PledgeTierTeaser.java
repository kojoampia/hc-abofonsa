package net.jojoaddison.abofonsa.preview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PledgeTierCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A backer tier shown on the pledge teaser. `handoffUrl` is the deep link into
 * fund.abofonsa.com — this app displays the offer and counts the click, and the crowdfunding
 * platform owns identity, payment, vouchers and certificates end to end.
 */
@Entity
@Table(name = "pledge_tier_teaser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PledgeTierTeaser implements Serializable {

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
    private PledgeTierCode code;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80, nullable = false)
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
    @Column(name = "blurb")
    private String blurb;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @DecimalMin(value = "0")
    @Column(name = "voucher_value", precision = 21, scale = 2)
    private BigDecimal voucherValue;

    @NotNull
    @Size(max = 512)
    @Column(name = "handoff_url", length = 512, nullable = false)
    private String handoffUrl;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull
    @Column(name = "published", nullable = false)
    private Boolean published;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tier")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tier" }, allowSetters = true)
    private Set<PledgeTierPerk> perks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PledgeTierTeaser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PledgeTierCode getCode() {
        return this.code;
    }

    public PledgeTierTeaser code(PledgeTierCode code) {
        this.setCode(code);
        return this;
    }

    public void setCode(PledgeTierCode code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public PledgeTierTeaser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlurb() {
        return this.blurb;
    }

    public PledgeTierTeaser blurb(String blurb) {
        this.setBlurb(blurb);
        return this;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public PledgeTierTeaser amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public PledgeTierTeaser currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getVoucherValue() {
        return this.voucherValue;
    }

    public PledgeTierTeaser voucherValue(BigDecimal voucherValue) {
        this.setVoucherValue(voucherValue);
        return this;
    }

    public void setVoucherValue(BigDecimal voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getHandoffUrl() {
        return this.handoffUrl;
    }

    public PledgeTierTeaser handoffUrl(String handoffUrl) {
        this.setHandoffUrl(handoffUrl);
        return this;
    }

    public void setHandoffUrl(String handoffUrl) {
        this.handoffUrl = handoffUrl;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public PledgeTierTeaser displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public PledgeTierTeaser published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Set<PledgeTierPerk> getPerks() {
        return this.perks;
    }

    public void setPerks(Set<PledgeTierPerk> pledgeTierPerks) {
        if (this.perks != null) {
            this.perks.forEach(i -> i.setTier(null));
        }
        if (pledgeTierPerks != null) {
            pledgeTierPerks.forEach(i -> i.setTier(this));
        }
        this.perks = pledgeTierPerks;
    }

    public PledgeTierTeaser perks(Set<PledgeTierPerk> pledgeTierPerks) {
        this.setPerks(pledgeTierPerks);
        return this;
    }

    public PledgeTierTeaser addPerk(PledgeTierPerk pledgeTierPerk) {
        this.perks.add(pledgeTierPerk);
        pledgeTierPerk.setTier(this);
        return this;
    }

    public PledgeTierTeaser removePerk(PledgeTierPerk pledgeTierPerk) {
        this.perks.remove(pledgeTierPerk);
        pledgeTierPerk.setTier(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PledgeTierTeaser)) {
            return false;
        }
        return getId() != null && getId().equals(((PledgeTierTeaser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PledgeTierTeaser{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", blurb='" + getBlurb() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", voucherValue=" + getVoucherValue() +
            ", handoffUrl='" + getHandoffUrl() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
