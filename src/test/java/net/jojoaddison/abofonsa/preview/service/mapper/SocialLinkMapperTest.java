package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.SocialLinkAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.SocialLinkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocialLinkMapperTest {

    private SocialLinkMapper socialLinkMapper;

    @BeforeEach
    void setUp() {
        socialLinkMapper = new SocialLinkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSocialLinkSample1();
        var actual = socialLinkMapper.toEntity(socialLinkMapper.toDto(expected));
        assertSocialLinkAllPropertiesEquals(expected, actual);
    }
}
