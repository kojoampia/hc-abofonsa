package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MetricRollupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricRollupDTO.class);
        MetricRollupDTO metricRollupDTO1 = new MetricRollupDTO();
        metricRollupDTO1.setId(1L);
        MetricRollupDTO metricRollupDTO2 = new MetricRollupDTO();
        assertThat(metricRollupDTO1).isNotEqualTo(metricRollupDTO2);
        metricRollupDTO2.setId(metricRollupDTO1.getId());
        assertThat(metricRollupDTO1).isEqualTo(metricRollupDTO2);
        metricRollupDTO2.setId(2L);
        assertThat(metricRollupDTO1).isNotEqualTo(metricRollupDTO2);
        metricRollupDTO1.setId(null);
        assertThat(metricRollupDTO1).isNotEqualTo(metricRollupDTO2);
    }
}
