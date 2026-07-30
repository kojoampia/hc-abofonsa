package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.CareServiceTeaserTestSamples.*;
import static net.jojoaddison.abofonsa.preview.domain.ServiceHighlightTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CareServiceTeaserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CareServiceTeaser.class);
        CareServiceTeaser careServiceTeaser1 = getCareServiceTeaserSample1();
        CareServiceTeaser careServiceTeaser2 = new CareServiceTeaser();
        assertThat(careServiceTeaser1).isNotEqualTo(careServiceTeaser2);

        careServiceTeaser2.setId(careServiceTeaser1.getId());
        assertThat(careServiceTeaser1).isEqualTo(careServiceTeaser2);

        careServiceTeaser2 = getCareServiceTeaserSample2();
        assertThat(careServiceTeaser1).isNotEqualTo(careServiceTeaser2);
    }

    @Test
    void highlightTest() {
        CareServiceTeaser careServiceTeaser = getCareServiceTeaserRandomSampleGenerator();
        ServiceHighlight serviceHighlightBack = getServiceHighlightRandomSampleGenerator();

        careServiceTeaser.addHighlight(serviceHighlightBack);
        assertThat(careServiceTeaser.getHighlights()).containsOnly(serviceHighlightBack);
        assertThat(serviceHighlightBack.getService()).isEqualTo(careServiceTeaser);

        careServiceTeaser.removeHighlight(serviceHighlightBack);
        assertThat(careServiceTeaser.getHighlights()).doesNotContain(serviceHighlightBack);
        assertThat(serviceHighlightBack.getService()).isNull();

        careServiceTeaser.highlights(new HashSet<>(Set.of(serviceHighlightBack)));
        assertThat(careServiceTeaser.getHighlights()).containsOnly(serviceHighlightBack);
        assertThat(serviceHighlightBack.getService()).isEqualTo(careServiceTeaser);

        careServiceTeaser.setHighlights(new HashSet<>());
        assertThat(careServiceTeaser.getHighlights()).doesNotContain(serviceHighlightBack);
        assertThat(serviceHighlightBack.getService()).isNull();
    }
}
