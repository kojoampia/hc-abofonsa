package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.LaunchMilestone;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LaunchMilestone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaunchMilestoneRepository extends JpaRepository<LaunchMilestone, Long> {}
