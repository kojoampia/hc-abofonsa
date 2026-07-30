package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CareServiceTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CareServiceTeaserRepository extends JpaRepository<CareServiceTeaser, Long> {}
