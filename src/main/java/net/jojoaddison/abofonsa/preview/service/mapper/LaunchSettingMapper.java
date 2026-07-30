package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import net.jojoaddison.abofonsa.preview.service.dto.LaunchSettingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LaunchSetting} and its DTO {@link LaunchSettingDTO}.
 */
@Mapper(componentModel = "spring")
public interface LaunchSettingMapper extends EntityMapper<LaunchSettingDTO, LaunchSetting> {}
