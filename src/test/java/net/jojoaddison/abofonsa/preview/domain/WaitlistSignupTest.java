package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.WaitlistSignupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WaitlistSignupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaitlistSignup.class);
        WaitlistSignup waitlistSignup1 = getWaitlistSignupSample1();
        WaitlistSignup waitlistSignup2 = new WaitlistSignup();
        assertThat(waitlistSignup1).isNotEqualTo(waitlistSignup2);

        waitlistSignup2.setId(waitlistSignup1.getId());
        assertThat(waitlistSignup1).isEqualTo(waitlistSignup2);

        waitlistSignup2 = getWaitlistSignupSample2();
        assertThat(waitlistSignup1).isNotEqualTo(waitlistSignup2);
    }
}
