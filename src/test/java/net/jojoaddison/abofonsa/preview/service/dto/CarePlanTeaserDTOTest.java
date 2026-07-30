package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarePlanTeaserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarePlanTeaserDTO.class);
        CarePlanTeaserDTO carePlanTeaserDTO1 = new CarePlanTeaserDTO();
        carePlanTeaserDTO1.setId(1L);
        CarePlanTeaserDTO carePlanTeaserDTO2 = new CarePlanTeaserDTO();
        assertThat(carePlanTeaserDTO1).isNotEqualTo(carePlanTeaserDTO2);
        carePlanTeaserDTO2.setId(carePlanTeaserDTO1.getId());
        assertThat(carePlanTeaserDTO1).isEqualTo(carePlanTeaserDTO2);
        carePlanTeaserDTO2.setId(2L);
        assertThat(carePlanTeaserDTO1).isNotEqualTo(carePlanTeaserDTO2);
        carePlanTeaserDTO1.setId(null);
        assertThat(carePlanTeaserDTO1).isNotEqualTo(carePlanTeaserDTO2);
    }
}
