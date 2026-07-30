package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.LaunchSettingAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.LaunchSettingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LaunchSettingMapperTest {

    private LaunchSettingMapper launchSettingMapper;

    @BeforeEach
    void setUp() {
        launchSettingMapper = new LaunchSettingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLaunchSettingSample1();
        var actual = launchSettingMapper.toEntity(launchSettingMapper.toDto(expected));
        assertLaunchSettingAllPropertiesEquals(expected, actual);
    }
}
