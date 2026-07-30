package net.jojoaddison.abofonsa.preview.service.mapper;

import static net.jojoaddison.abofonsa.preview.domain.DataExportLogAsserts.*;
import static net.jojoaddison.abofonsa.preview.domain.DataExportLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataExportLogMapperTest {

    private DataExportLogMapper dataExportLogMapper;

    @BeforeEach
    void setUp() {
        dataExportLogMapper = new DataExportLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDataExportLogSample1();
        var actual = dataExportLogMapper.toEntity(dataExportLogMapper.toDto(expected));
        assertDataExportLogAllPropertiesEquals(expected, actual);
    }
}
