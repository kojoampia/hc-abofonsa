package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.DataExportLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DataExportLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataExportLogRepository extends JpaRepository<DataExportLog, Long>, JpaSpecificationExecutor<DataExportLog> {}
