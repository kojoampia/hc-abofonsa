package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.CaptureEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CaptureEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CaptureEvent.class);
        CaptureEvent captureEvent1 = getCaptureEventSample1();
        CaptureEvent captureEvent2 = new CaptureEvent();
        assertThat(captureEvent1).isNotEqualTo(captureEvent2);

        captureEvent2.setId(captureEvent1.getId());
        assertThat(captureEvent1).isEqualTo(captureEvent2);

        captureEvent2 = getCaptureEventSample2();
        assertThat(captureEvent1).isNotEqualTo(captureEvent2);
    }
}
