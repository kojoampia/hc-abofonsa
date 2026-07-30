package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import net.jojoaddison.abofonsa.preview.domain.PlanFeature;
import net.jojoaddison.abofonsa.preview.service.dto.CarePlanTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.dto.PlanFeatureDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanFeature} and its DTO {@link PlanFeatureDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanFeatureMapper extends EntityMapper<PlanFeatureDTO, PlanFeature> {
    @Mapping(target = "plan", source = "plan", qualifiedByName = "carePlanTeaserName")
    PlanFeatureDTO toDto(PlanFeature s);

    @Named("carePlanTeaserName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CarePlanTeaserDTO toDtoCarePlanTeaserName(CarePlanTeaser carePlanTeaser);
}
