package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceHighlightDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceHighlightDTO.class);
        ServiceHighlightDTO serviceHighlightDTO1 = new ServiceHighlightDTO();
        serviceHighlightDTO1.setId(1L);
        ServiceHighlightDTO serviceHighlightDTO2 = new ServiceHighlightDTO();
        assertThat(serviceHighlightDTO1).isNotEqualTo(serviceHighlightDTO2);
        serviceHighlightDTO2.setId(serviceHighlightDTO1.getId());
        assertThat(serviceHighlightDTO1).isEqualTo(serviceHighlightDTO2);
        serviceHighlightDTO2.setId(2L);
        assertThat(serviceHighlightDTO1).isNotEqualTo(serviceHighlightDTO2);
        serviceHighlightDTO1.setId(null);
        assertThat(serviceHighlightDTO1).isNotEqualTo(serviceHighlightDTO2);
    }
}
