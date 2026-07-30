package net.jojoaddison.abofonsa.preview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import net.jojoaddison.abofonsa.preview.domain.enumeration.PlanCode;

/**
 * A DTO for the {@link net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser} entity.
 */
@Schema(description = "PEAR / PAWPAW / MELON, with the same GHS pricing the main site quotes.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarePlanTeaserDTO implements Serializable {

    private Long id;

    @NotNull
    private PlanCode code;

    @NotNull
    @Size(max = 80)
    private String name;

    @Lob
    private String forWho;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal priceAmount;

    @NotNull
    @Size(max = 3)
    private String priceCurrency;

    @NotNull
    @Size(max = 20)
    private String pricePeriod;

    @Size(max = 255)
    private String priceNote;

    @NotNull
    private Boolean featured;

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

    public PlanCode getCode() {
        return code;
    }

    public void setCode(PlanCode code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForWho() {
        return forWho;
    }

    public void setForWho(String forWho) {
        this.forWho = forWho;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public String getPricePeriod() {
        return pricePeriod;
    }

    public void setPricePeriod(String pricePeriod) {
        this.pricePeriod = pricePeriod;
    }

    public String getPriceNote() {
        return priceNote;
    }

    public void setPriceNote(String priceNote) {
        this.priceNote = priceNote;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
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
        if (!(o instanceof CarePlanTeaserDTO)) {
            return false;
        }

        CarePlanTeaserDTO carePlanTeaserDTO = (CarePlanTeaserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carePlanTeaserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarePlanTeaserDTO{" +
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
