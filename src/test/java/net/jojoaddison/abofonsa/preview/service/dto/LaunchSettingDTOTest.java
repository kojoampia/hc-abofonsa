package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LaunchSettingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaunchSettingDTO.class);
        LaunchSettingDTO launchSettingDTO1 = new LaunchSettingDTO();
        launchSettingDTO1.setId(1L);
        LaunchSettingDTO launchSettingDTO2 = new LaunchSettingDTO();
        assertThat(launchSettingDTO1).isNotEqualTo(launchSettingDTO2);
        launchSettingDTO2.setId(launchSettingDTO1.getId());
        assertThat(launchSettingDTO1).isEqualTo(launchSettingDTO2);
        launchSettingDTO2.setId(2L);
        assertThat(launchSettingDTO1).isNotEqualTo(launchSettingDTO2);
        launchSettingDTO1.setId(null);
        assertThat(launchSettingDTO1).isNotEqualTo(launchSettingDTO2);
    }
}
