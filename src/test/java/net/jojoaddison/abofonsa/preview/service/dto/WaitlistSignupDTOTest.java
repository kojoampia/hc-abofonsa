package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WaitlistSignupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaitlistSignupDTO.class);
        WaitlistSignupDTO waitlistSignupDTO1 = new WaitlistSignupDTO();
        waitlistSignupDTO1.setId(1L);
        WaitlistSignupDTO waitlistSignupDTO2 = new WaitlistSignupDTO();
        assertThat(waitlistSignupDTO1).isNotEqualTo(waitlistSignupDTO2);
        waitlistSignupDTO2.setId(waitlistSignupDTO1.getId());
        assertThat(waitlistSignupDTO1).isEqualTo(waitlistSignupDTO2);
        waitlistSignupDTO2.setId(2L);
        assertThat(waitlistSignupDTO1).isNotEqualTo(waitlistSignupDTO2);
        waitlistSignupDTO1.setId(null);
        assertThat(waitlistSignupDTO1).isNotEqualTo(waitlistSignupDTO2);
    }
}
