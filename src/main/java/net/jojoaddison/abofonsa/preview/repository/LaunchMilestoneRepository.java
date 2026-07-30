package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.LaunchMilestone;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LaunchMilestone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaunchMilestoneRepository extends JpaRepository<LaunchMilestone, Long> {
    List<LaunchMilestone> findByPublishedIsTrueOrderByDisplayOrderAsc();
}
