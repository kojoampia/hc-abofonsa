package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LaunchMilestoneDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaunchMilestoneDTO.class);
        LaunchMilestoneDTO launchMilestoneDTO1 = new LaunchMilestoneDTO();
        launchMilestoneDTO1.setId(1L);
        LaunchMilestoneDTO launchMilestoneDTO2 = new LaunchMilestoneDTO();
        assertThat(launchMilestoneDTO1).isNotEqualTo(launchMilestoneDTO2);
        launchMilestoneDTO2.setId(launchMilestoneDTO1.getId());
        assertThat(launchMilestoneDTO1).isEqualTo(launchMilestoneDTO2);
        launchMilestoneDTO2.setId(2L);
        assertThat(launchMilestoneDTO1).isNotEqualTo(launchMilestoneDTO2);
        launchMilestoneDTO1.setId(null);
        assertThat(launchMilestoneDTO1).isNotEqualTo(launchMilestoneDTO2);
    }
}
