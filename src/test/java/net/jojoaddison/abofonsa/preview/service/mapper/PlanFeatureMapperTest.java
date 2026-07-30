package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.PlanFeatureAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.PlanFeatureTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanFeatureMapperTest {

    private PlanFeatureMapper planFeatureMapper;

    @BeforeEach
    void setUp() {
        planFeatureMapper = new PlanFeatureMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanFeatureSample1();
        var actual = planFeatureMapper.toEntity(planFeatureMapper.toDto(expected));
        assertPlanFeatureAllPropertiesEquals(expected, actual);
    }
}
