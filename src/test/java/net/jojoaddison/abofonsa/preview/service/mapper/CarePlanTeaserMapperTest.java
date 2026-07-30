package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.CarePlanTeaserAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.CarePlanTeaserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarePlanTeaserMapperTest {

    private CarePlanTeaserMapper carePlanTeaserMapper;

    @BeforeEach
    void setUp() {
        carePlanTeaserMapper = new CarePlanTeaserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarePlanTeaserSample1();
        var actual = carePlanTeaserMapper.toEntity(carePlanTeaserMapper.toDto(expected));
        assertCarePlanTeaserAllPropertiesEquals(expected, actual);
    }
}
