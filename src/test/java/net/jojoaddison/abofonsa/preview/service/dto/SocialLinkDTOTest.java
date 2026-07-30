package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocialLinkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocialLinkDTO.class);
        SocialLinkDTO socialLinkDTO1 = new SocialLinkDTO();
        socialLinkDTO1.setId(1L);
        SocialLinkDTO socialLinkDTO2 = new SocialLinkDTO();
        assertThat(socialLinkDTO1).isNotEqualTo(socialLinkDTO2);
        socialLinkDTO2.setId(socialLinkDTO1.getId());
        assertThat(socialLinkDTO1).isEqualTo(socialLinkDTO2);
        socialLinkDTO2.setId(2L);
        assertThat(socialLinkDTO1).isNotEqualTo(socialLinkDTO2);
        socialLinkDTO1.setId(null);
        assertThat(socialLinkDTO1).isNotEqualTo(socialLinkDTO2);
    }
}
