package net.jojoaddison.abofonsa.preview.domain;

import static net.jojoaddison.abofonsa.preview.domain.DataExportLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import net.jojoaddison.abofonsa.preview.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DataExportLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataExportLog.class);
        DataExportLog dataExportLog1 = getDataExportLogSample1();
        DataExportLog dataExportLog2 = new DataExportLog();
        assertThat(dataExportLog1).isNotEqualTo(dataExportLog2);

        dataExportLog2.setId(dataExportLog1.getId());
        assertThat(dataExportLog1).isEqualTo(dataExportLog2);

        dataExportLog2 = getDataExportLogSample2();
        assertThat(dataExportLog1).isNotEqualTo(dataExportLog2);
    }
}
