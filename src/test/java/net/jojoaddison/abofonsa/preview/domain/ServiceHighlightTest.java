package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.CareServiceTeaserTestSamples.*;
import static net.jojoaddison.abofonsa.preview.domain.ServiceHighlightTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceHighlightTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceHighlight.class);
        ServiceHighlight serviceHighlight1 = getServiceHighlightSample1();
        ServiceHighlight serviceHighlight2 = new ServiceHighlight();
        assertThat(serviceHighlight1).isNotEqualTo(serviceHighlight2);

        serviceHighlight2.setId(serviceHighlight1.getId());
        assertThat(serviceHighlight1).isEqualTo(serviceHighlight2);

        serviceHighlight2 = getServiceHighlightSample2();
        assertThat(serviceHighlight1).isNotEqualTo(serviceHighlight2);
    }

    @Test
    void serviceTest() {
        ServiceHighlight serviceHighlight = getServiceHighlightRandomSampleGenerator();
        CareServiceTeaser careServiceTeaserBack = getCareServiceTeaserRandomSampleGenerator();

        serviceHighlight.setService(careServiceTeaserBack);
        assertThat(serviceHighlight.getService()).isEqualTo(careServiceTeaserBack);

        serviceHighlight.service(null);
        assertThat(serviceHighlight.getService()).isNull();
    }
}
