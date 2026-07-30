package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.CareServiceTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CareServiceTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CareServiceTeaserRepository extends JpaRepository<CareServiceTeaser, Long> {
    /**
     * The published services with their highlights, ordered for display.
     *
     * <p>The fetch join is what stops this being six extra queries — highlights are LAZY, and the
     * public payload always needs them. Hibernate returns the collection unordered regardless, so
     * the service sorts highlights by displayOrder after loading.
     */
    @Query("select distinct s from CareServiceTeaser s left join fetch s.highlights where s.published = true order by s.displayOrder")
    List<CareServiceTeaser> findPublishedWithHighlights();
}
