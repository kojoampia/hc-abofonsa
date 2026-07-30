package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CareServiceTeaserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CareServiceTeaserDTO.class);
        CareServiceTeaserDTO careServiceTeaserDTO1 = new CareServiceTeaserDTO();
        careServiceTeaserDTO1.setId(1L);
        CareServiceTeaserDTO careServiceTeaserDTO2 = new CareServiceTeaserDTO();
        assertThat(careServiceTeaserDTO1).isNotEqualTo(careServiceTeaserDTO2);
        careServiceTeaserDTO2.setId(careServiceTeaserDTO1.getId());
        assertThat(careServiceTeaserDTO1).isEqualTo(careServiceTeaserDTO2);
        careServiceTeaserDTO2.setId(2L);
        assertThat(careServiceTeaserDTO1).isNotEqualTo(careServiceTeaserDTO2);
        careServiceTeaserDTO1.setId(null);
        assertThat(careServiceTeaserDTO1).isNotEqualTo(careServiceTeaserDTO2);
    }
}
