package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.LaunchMilestoneTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LaunchMilestoneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaunchMilestone.class);
        LaunchMilestone launchMilestone1 = getLaunchMilestoneSample1();
        LaunchMilestone launchMilestone2 = new LaunchMilestone();
        assertThat(launchMilestone1).isNotEqualTo(launchMilestone2);

        launchMilestone2.setId(launchMilestone1.getId());
        assertThat(launchMilestone1).isEqualTo(launchMilestone2);

        launchMilestone2 = getLaunchMilestoneSample2();
        assertThat(launchMilestone1).isNotEqualTo(launchMilestone2);
    }
}
