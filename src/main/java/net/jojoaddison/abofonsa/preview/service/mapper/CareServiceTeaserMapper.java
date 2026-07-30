package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import net.jojoaddison.abofonsa.preview.service.dto.CareServiceTeaserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CareServiceTeaser} and its DTO {@link CareServiceTeaserDTO}.
 */
@Mapper(componentModel = "spring")
public interface CareServiceTeaserMapper extends EntityMapper<CareServiceTeaserDTO, CareServiceTeaser> {}
