package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaserAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PledgeTierTeaserMapperTest {

    private PledgeTierTeaserMapper pledgeTierTeaserMapper;

    @BeforeEach
    void setUp() {
        pledgeTierTeaserMapper = new PledgeTierTeaserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPledgeTierTeaserSample1();
        var actual = pledgeTierTeaserMapper.toEntity(pledgeTierTeaserMapper.toDto(expected));
        assertPledgeTierTeaserAllPropertiesEquals(expected, actual);
    }
}
