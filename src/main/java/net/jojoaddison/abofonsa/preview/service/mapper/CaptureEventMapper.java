package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import net.jojoaddison.abofonsa.preview.service.dto.CaptureEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CaptureEvent} and its DTO {@link CaptureEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface CaptureEventMapper extends EntityMapper<CaptureEventDTO, CaptureEvent> {}
