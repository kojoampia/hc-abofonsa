package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.LaunchMilestone;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchMilestoneDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LaunchMilestone} and its DTO {@link LaunchMilestoneDTO}.
 */
@Mapper(componentModel = "spring")
public interface LaunchMilestoneMapper extends EntityMapper<LaunchMilestoneDTO, LaunchMilestone> {}
