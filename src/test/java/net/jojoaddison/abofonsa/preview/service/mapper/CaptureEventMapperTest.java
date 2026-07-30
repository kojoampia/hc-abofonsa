package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.CaptureEventAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.CaptureEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaptureEventMapperTest {

    private CaptureEventMapper captureEventMapper;

    @BeforeEach
    void setUp() {
        captureEventMapper = new CaptureEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCaptureEventSample1();
        var actual = captureEventMapper.toEntity(captureEventMapper.toDto(expected));
        assertCaptureEventAllPropertiesEquals(expected, actual);
    }
}
