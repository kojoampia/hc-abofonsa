package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.WaitlistSignupAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.WaitlistSignupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaitlistSignupMapperTest {

    private WaitlistSignupMapper waitlistSignupMapper;

    @BeforeEach
    void setUp() {
        waitlistSignupMapper = new WaitlistSignupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWaitlistSignupSample1();
        var actual = waitlistSignupMapper.toEntity(waitlistSignupMapper.toDto(expected));
        assertWaitlistSignupAllPropertiesEquals(expected, actual);
    }
}
