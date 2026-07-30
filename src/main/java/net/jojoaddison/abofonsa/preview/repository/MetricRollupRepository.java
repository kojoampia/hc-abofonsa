package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.MetricRollup;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MetricRollup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricRollupRepository extends JpaRepository<MetricRollup, Long>, JpaSpecificationExecutor<MetricRollup> {}
