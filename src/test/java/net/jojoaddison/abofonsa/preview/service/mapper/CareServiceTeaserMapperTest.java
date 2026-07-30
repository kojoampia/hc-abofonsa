package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.CareServiceTeaserAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.CareServiceTeaserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CareServiceTeaserMapperTest {

    private CareServiceTeaserMapper careServiceTeaserMapper;

    @BeforeEach
    void setUp() {
        careServiceTeaserMapper = new CareServiceTeaserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCareServiceTeaserSample1();
        var actual = careServiceTeaserMapper.toEntity(careServiceTeaserMapper.toDto(expected));
        assertCareServiceTeaserAllPropertiesEquals(expected, actual);
    }
}
