package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierPerkDTO;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierTeaserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PledgeTierPerk} and its DTO {@link PledgeTierPerkDTO}.
 */
@Mapper(componentModel = "spring")
public interface PledgeTierPerkMapper extends EntityMapper<PledgeTierPerkDTO, PledgeTierPerk> {
    @Mapping(target = "tier", source = "tier", qualifiedByName = "pledgeTierTeaserName")
    PledgeTierPerkDTO toDto(PledgeTierPerk s);

    @Named("pledgeTierTeaserName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PledgeTierTeaserDTO toDtoPledgeTierTeaserName(PledgeTierTeaser pledgeTierTeaser);
}
