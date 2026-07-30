package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.CaptureEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CaptureEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CaptureEventRepository extends JpaRepository<CaptureEvent, Long>, JpaSpecificationExecutor<CaptureEvent> {}
