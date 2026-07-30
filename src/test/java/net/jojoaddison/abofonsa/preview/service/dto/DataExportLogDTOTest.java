package net.jojoaddison.abofonsa.preview.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DataExportLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataExportLogDTO.class);
        DataExportLogDTO dataExportLogDTO1 = new DataExportLogDTO();
        dataExportLogDTO1.setId(1L);
        DataExportLogDTO dataExportLogDTO2 = new DataExportLogDTO();
        assertThat(dataExportLogDTO1).isNotEqualTo(dataExportLogDTO2);
        dataExportLogDTO2.setId(dataExportLogDTO1.getId());
        assertThat(dataExportLogDTO1).isEqualTo(dataExportLogDTO2);
        dataExportLogDTO2.setId(2L);
        assertThat(dataExportLogDTO1).isNotEqualTo(dataExportLogDTO2);
        dataExportLogDTO1.setId(null);
        assertThat(dataExportLogDTO1).isNotEqualTo(dataExportLogDTO2);
    }
}
