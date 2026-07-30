package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.CarePlanTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CarePlanTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarePlanTeaserRepository extends JpaRepository<CarePlanTeaser, Long> {
    @Query("select distinct p from CarePlanTeaser p left join fetch p.features where p.published = true order by p.displayOrder")
    List<CarePlanTeaser> findPublishedWithFeatures();
}
