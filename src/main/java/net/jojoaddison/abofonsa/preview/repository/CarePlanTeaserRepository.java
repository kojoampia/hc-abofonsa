package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CarePlanTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarePlanTeaserRepository extends JpaRepository<CarePlanTeaser, Long> {}
