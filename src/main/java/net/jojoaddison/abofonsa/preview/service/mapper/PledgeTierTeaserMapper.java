package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import net.jojoaddison.abofonsa.preview.service.dto.PledgeTierTeaserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PledgeTierTeaser} and its DTO {@link PledgeTierTeaserDTO}.
 */
@Mapper(componentModel = "spring")
public interface PledgeTierTeaserMapper extends EntityMapper<PledgeTierTeaserDTO, PledgeTierTeaser> {}
