package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.ServiceHighlightAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.ServiceHighlightTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceHighlightMapperTest {

    private ServiceHighlightMapper serviceHighlightMapper;

    @BeforeEach
    void setUp() {
        serviceHighlightMapper = new ServiceHighlightMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceHighlightSample1();
        var actual = serviceHighlightMapper.toEntity(serviceHighlightMapper.toDto(expected));
        assertServiceHighlightAllPropertiesEquals(expected, actual);
    }
}
