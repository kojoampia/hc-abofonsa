package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PledgeTierPerkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PledgeTierPerkDTO.class);
        PledgeTierPerkDTO pledgeTierPerkDTO1 = new PledgeTierPerkDTO();
        pledgeTierPerkDTO1.setId(1L);
        PledgeTierPerkDTO pledgeTierPerkDTO2 = new PledgeTierPerkDTO();
        assertThat(pledgeTierPerkDTO1).isNotEqualTo(pledgeTierPerkDTO2);
        pledgeTierPerkDTO2.setId(pledgeTierPerkDTO1.getId());
        assertThat(pledgeTierPerkDTO1).isEqualTo(pledgeTierPerkDTO2);
        pledgeTierPerkDTO2.setId(2L);
        assertThat(pledgeTierPerkDTO1).isNotEqualTo(pledgeTierPerkDTO2);
        pledgeTierPerkDTO1.setId(null);
        assertThat(pledgeTierPerkDTO1).isNotEqualTo(pledgeTierPerkDTO2);
    }
}
