package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import net.jojoaddison.abofonsa.preview.service.dto.MetricRollupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MetricRollup} and its DTO {@link MetricRollupDTO}.
 */
@Mapper(componentModel = "spring")
public interface MetricRollupMapper extends EntityMapper<MetricRollupDTO, MetricRollup> {}
