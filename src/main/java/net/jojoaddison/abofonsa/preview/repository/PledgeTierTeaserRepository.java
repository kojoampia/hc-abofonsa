package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PledgeTierTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PledgeTierTeaserRepository extends JpaRepository<PledgeTierTeaser, Long> {
    @Query("select distinct t from PledgeTierTeaser t left join fetch t.perks where t.published = true order by t.displayOrder")
    List<PledgeTierTeaser> findPublishedWithPerks();
}
