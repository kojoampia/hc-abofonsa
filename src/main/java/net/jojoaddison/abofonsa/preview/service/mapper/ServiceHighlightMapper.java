package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import net.jojoaddison.abofonsa.preview.domain.ServiceHighlight;
import net.jojoaddison.abofonsa.preview.service.dto.CareServiceTeaserDTO;
import net.jojoaddison.abofonsa.preview.service.dto.ServiceHighlightDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServiceHighlight} and its DTO {@link ServiceHighlightDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceHighlightMapper extends EntityMapper<ServiceHighlightDTO, ServiceHighlight> {
    @Mapping(target = "service", source = "service", qualifiedByName = "careServiceTeaserName")
    ServiceHighlightDTO toDto(ServiceHighlight s);

    @Named("careServiceTeaserName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CareServiceTeaserDTO toDtoCareServiceTeaserName(CareServiceTeaser careServiceTeaser);
}
