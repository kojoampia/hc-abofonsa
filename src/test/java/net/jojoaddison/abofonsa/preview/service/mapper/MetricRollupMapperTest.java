package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.MetricRollupAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.MetricRollupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricRollupMapperTest {

    private MetricRollupMapper metricRollupMapper;

    @BeforeEach
    void setUp() {
        metricRollupMapper = new MetricRollupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMetricRollupSample1();
        var actual = metricRollupMapper.toEntity(metricRollupMapper.toDto(expected));
        assertMetricRollupAllPropertiesEquals(expected, actual);
    }
}
