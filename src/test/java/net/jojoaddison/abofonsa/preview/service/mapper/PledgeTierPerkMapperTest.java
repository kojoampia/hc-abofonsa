package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.PledgeTierPerkAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.PledgeTierPerkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PledgeTierPerkMapperTest {

    private PledgeTierPerkMapper pledgeTierPerkMapper;

    @BeforeEach
    void setUp() {
        pledgeTierPerkMapper = new PledgeTierPerkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPledgeTierPerkSample1();
        var actual = pledgeTierPerkMapper.toEntity(pledgeTierPerkMapper.toDto(expected));
        assertPledgeTierPerkAllPropertiesEquals(expected, actual);
    }
}
