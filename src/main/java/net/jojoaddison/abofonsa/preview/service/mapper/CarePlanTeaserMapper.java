package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import net.jojoaddison.abofonsa.preview.service.dto.CarePlanTeaserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CarePlanTeaser} and its DTO {@link CarePlanTeaserDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarePlanTeaserMapper extends EntityMapper<CarePlanTeaserDTO, CarePlanTeaser> {}
