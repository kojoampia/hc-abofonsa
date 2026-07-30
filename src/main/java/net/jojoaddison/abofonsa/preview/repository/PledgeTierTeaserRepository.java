package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.PledgeTierTeaser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PledgeTierTeaser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PledgeTierTeaserRepository extends JpaRepository<PledgeTierTeaser, Long> {}
