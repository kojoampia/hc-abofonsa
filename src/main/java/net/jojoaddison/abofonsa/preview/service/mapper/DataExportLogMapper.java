package net.jojoaddison.abofonsa.preview.service.mapper;

import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import net.jojoaddison.abofonsa.preview.service.dto.DataExportLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DataExportLog} and its DTO {@link DataExportLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface DataExportLogMapper extends EntityMapper<DataExportLogDTO, DataExportLog> {}
