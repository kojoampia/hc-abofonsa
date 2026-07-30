package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.SocialLinkTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocialLinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocialLink.class);
        SocialLink socialLink1 = getSocialLinkSample1();
        SocialLink socialLink2 = new SocialLink();
        assertThat(socialLink1).isNotEqualTo(socialLink2);

        socialLink2.setId(socialLink1.getId());
        assertThat(socialLink1).isEqualTo(socialLink2);

        socialLink2 = getSocialLinkSample2();
        assertThat(socialLink1).isNotEqualTo(socialLink2);
    }
}
