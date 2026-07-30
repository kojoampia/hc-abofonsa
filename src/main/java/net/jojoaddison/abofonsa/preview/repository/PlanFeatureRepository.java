package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.PlanFeature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PlanFeature entity.
 */
@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {
    default Optional<PlanFeature> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PlanFeature> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PlanFeature> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select planFeature from PlanFeature planFeature left join fetch planFeature.plan",
        countQuery = "select count(planFeature) from PlanFeature planFeature"
    )
    Page<PlanFeature> findAllWithToOneRelationships(Pageable pageable);

    @Query("select planFeature from PlanFeature planFeature left join fetch planFeature.plan")
    List<PlanFeature> findAllWithToOneRelationships();

    @Query("select planFeature from PlanFeature planFeature left join fetch planFeature.plan where planFeature.id =:id")
    Optional<PlanFeature> findOneWithToOneRelationships(@Param("id") Long id);
}
