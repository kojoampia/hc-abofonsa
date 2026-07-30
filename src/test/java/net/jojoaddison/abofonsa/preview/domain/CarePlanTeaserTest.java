package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.CarePlanTeaserTestSamples.*;
import static net.jojoaddison.abofonsa.preview.domain.PlanFeatureTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarePlanTeaserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarePlanTeaser.class);
        CarePlanTeaser carePlanTeaser1 = getCarePlanTeaserSample1();
        CarePlanTeaser carePlanTeaser2 = new CarePlanTeaser();
        assertThat(carePlanTeaser1).isNotEqualTo(carePlanTeaser2);

        carePlanTeaser2.setId(carePlanTeaser1.getId());
        assertThat(carePlanTeaser1).isEqualTo(carePlanTeaser2);

        carePlanTeaser2 = getCarePlanTeaserSample2();
        assertThat(carePlanTeaser1).isNotEqualTo(carePlanTeaser2);
    }

    @Test
    void featureTest() {
        CarePlanTeaser carePlanTeaser = getCarePlanTeaserRandomSampleGenerator();
        PlanFeature planFeatureBack = getPlanFeatureRandomSampleGenerator();

        carePlanTeaser.addFeature(planFeatureBack);
        assertThat(carePlanTeaser.getFeatures()).containsOnly(planFeatureBack);
        assertThat(planFeatureBack.getPlan()).isEqualTo(carePlanTeaser);

        carePlanTeaser.removeFeature(planFeatureBack);
        assertThat(carePlanTeaser.getFeatures()).doesNotContain(planFeatureBack);
        assertThat(planFeatureBack.getPlan()).isNull();

        carePlanTeaser.features(new HashSet<>(Set.of(planFeatureBack)));
        assertThat(carePlanTeaser.getFeatures()).containsOnly(planFeatureBack);
        assertThat(planFeatureBack.getPlan()).isEqualTo(carePlanTeaser);

        carePlanTeaser.setFeatures(new HashSet<>());
        assertThat(carePlanTeaser.getFeatures()).doesNotContain(planFeatureBack);
        assertThat(planFeatureBack.getPlan()).isNull();
    }
}
