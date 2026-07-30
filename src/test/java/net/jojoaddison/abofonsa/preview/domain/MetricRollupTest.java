package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.MetricRollupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MetricRollupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricRollup.class);
        MetricRollup metricRollup1 = getMetricRollupSample1();
        MetricRollup metricRollup2 = new MetricRollup();
        assertThat(metricRollup1).isNotEqualTo(metricRollup2);

        metricRollup2.setId(metricRollup1.getId());
        assertThat(metricRollup1).isEqualTo(metricRollup2);

        metricRollup2 = getMetricRollupSample2();
        assertThat(metricRollup1).isNotEqualTo(metricRollup2);
    }
}
