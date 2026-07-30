package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.SocialLink;
import net.jojoaddison.abofonsa.preview.service.dto.SocialLinkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SocialLink} and its DTO {@link SocialLinkDTO}.
 */
@Mapper(componentModel = "spring")
public interface SocialLinkMapper extends EntityMapper<SocialLinkDTO, SocialLink> {}
