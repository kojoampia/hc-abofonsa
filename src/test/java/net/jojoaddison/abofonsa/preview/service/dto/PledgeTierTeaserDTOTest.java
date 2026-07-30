package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PledgeTierTeaserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PledgeTierTeaserDTO.class);
        PledgeTierTeaserDTO pledgeTierTeaserDTO1 = new PledgeTierTeaserDTO();
        pledgeTierTeaserDTO1.setId(1L);
        PledgeTierTeaserDTO pledgeTierTeaserDTO2 = new PledgeTierTeaserDTO();
        assertThat(pledgeTierTeaserDTO1).isNotEqualTo(pledgeTierTeaserDTO2);
        pledgeTierTeaserDTO2.setId(pledgeTierTeaserDTO1.getId());
        assertThat(pledgeTierTeaserDTO1).isEqualTo(pledgeTierTeaserDTO2);
        pledgeTierTeaserDTO2.setId(2L);
        assertThat(pledgeTierTeaserDTO1).isNotEqualTo(pledgeTierTeaserDTO2);
        pledgeTierTeaserDTO1.setId(null);
        assertThat(pledgeTierTeaserDTO1).isNotEqualTo(pledgeTierTeaserDTO2);
    }
}
