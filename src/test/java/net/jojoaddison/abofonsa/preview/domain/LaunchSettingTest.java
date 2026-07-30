package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.LaunchSettingTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LaunchSettingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaunchSetting.class);
        LaunchSetting launchSetting1 = getLaunchSettingSample1();
        LaunchSetting launchSetting2 = new LaunchSetting();
        assertThat(launchSetting1).isNotEqualTo(launchSetting2);

        launchSetting2.setId(launchSetting1.getId());
        assertThat(launchSetting1).isEqualTo(launchSetting2);

        launchSetting2 = getLaunchSettingSample2();
        assertThat(launchSetting1).isNotEqualTo(launchSetting2);
    }
}
