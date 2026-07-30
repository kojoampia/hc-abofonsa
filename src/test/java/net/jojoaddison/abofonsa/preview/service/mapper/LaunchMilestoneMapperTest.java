package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.LaunchMilestoneAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.LaunchMilestoneTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LaunchMilestoneMapperTest {

    private LaunchMilestoneMapper launchMilestoneMapper;

    @BeforeEach
    void setUp() {
        launchMilestoneMapper = new LaunchMilestoneMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLaunchMilestoneSample1();
        var actual = launchMilestoneMapper.toEntity(launchMilestoneMapper.toDto(expected));
        assertLaunchMilestoneAllPropertiesEquals(expected, actual);
    }
}
