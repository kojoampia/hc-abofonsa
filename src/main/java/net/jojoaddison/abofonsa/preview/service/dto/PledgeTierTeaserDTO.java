package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PledgeTierCode;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser} entity.
 */
@Schema(
    description = "A backer tier shown on the pledge teaser. `handoffUrl` is the deep link into\nfund.abofonsa.com — this app displays the offer and counts the click, and the crowdfunding\nplatform owns identity, payment, vouchers and certificates end to end."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PledgeTierTeaserDTO implements Serializable {

    private Long id;

    @NotNull
    private PledgeTierCode code;

    @NotNull
    @Size(max = 80)
    private String name;

    @Lob
    private String blurb;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    private String currency;

    @DecimalMin(value = "0")
    private BigDecimal voucherValue;

    @NotNull
    @Size(max = 512)
    private String handoffUrl;

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

    public PledgeTierCode getCode() {
        return code;
    }

    public void setCode(PledgeTierCode code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(BigDecimal voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getHandoffUrl() {
        return handoffUrl;
    }

    public void setHandoffUrl(String handoffUrl) {
        this.handoffUrl = handoffUrl;
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
        if (!(o instanceof PledgeTierTeaserDTO)) {
            return false;
        }

        PledgeTierTeaserDTO pledgeTierTeaserDTO = (PledgeTierTeaserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pledgeTierTeaserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PledgeTierTeaserDTO{" +
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
