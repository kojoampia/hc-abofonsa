package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CaptureEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CaptureEventDTO.class);
        CaptureEventDTO captureEventDTO1 = new CaptureEventDTO();
        captureEventDTO1.setId(1L);
        CaptureEventDTO captureEventDTO2 = new CaptureEventDTO();
        assertThat(captureEventDTO1).isNotEqualTo(captureEventDTO2);
        captureEventDTO2.setId(captureEventDTO1.getId());
        assertThat(captureEventDTO1).isEqualTo(captureEventDTO2);
        captureEventDTO2.setId(2L);
        assertThat(captureEventDTO1).isNotEqualTo(captureEventDTO2);
        captureEventDTO1.setId(null);
        assertThat(captureEventDTO1).isNotEqualTo(captureEventDTO2);
    }
}
