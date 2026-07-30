package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import net.jojoaddison.abofonsa.preview.service.dto.WaitlistSignupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WaitlistSignup} and its DTO {@link WaitlistSignupDTO}.
 */
@Mapper(componentModel = "spring")
public interface WaitlistSignupMapper extends EntityMapper<WaitlistSignupDTO, WaitlistSignup> {}
