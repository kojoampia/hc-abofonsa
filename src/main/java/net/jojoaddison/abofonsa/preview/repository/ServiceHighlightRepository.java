package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.ServiceHighlight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ServiceHighlight entity.
 */
@Repository
public interface ServiceHighlightRepository extends JpaRepository<ServiceHighlight, Long> {
    default Optional<ServiceHighlight> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ServiceHighlight> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ServiceHighlight> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select serviceHighlight from ServiceHighlight serviceHighlight left join fetch serviceHighlight.service",
        countQuery = "select count(serviceHighlight) from ServiceHighlight serviceHighlight"
    )
    Page<ServiceHighlight> findAllWithToOneRelationships(Pageable pageable);

    @Query("select serviceHighlight from ServiceHighlight serviceHighlight left join fetch serviceHighlight.service")
    List<ServiceHighlight> findAllWithToOneRelationships();

    @Query(
        "select serviceHighlight from ServiceHighlight serviceHighlight left join fetch serviceHighlight.service where serviceHighlight.id =:id"
    )
    Optional<ServiceHighlight> findOneWithToOneRelationships(@Param("id") Long id);
}
