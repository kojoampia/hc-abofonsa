package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.PledgeTierPerk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PledgeTierPerk entity.
 */
@Repository
public interface PledgeTierPerkRepository extends JpaRepository<PledgeTierPerk, Long> {
    default Optional<PledgeTierPerk> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PledgeTierPerk> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PledgeTierPerk> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select pledgeTierPerk from PledgeTierPerk pledgeTierPerk left join fetch pledgeTierPerk.tier",
        countQuery = "select count(pledgeTierPerk) from PledgeTierPerk pledgeTierPerk"
    )
    Page<PledgeTierPerk> findAllWithToOneRelationships(Pageable pageable);

    @Query("select pledgeTierPerk from PledgeTierPerk pledgeTierPerk left join fetch pledgeTierPerk.tier")
    List<PledgeTierPerk> findAllWithToOneRelationships();

    @Query("select pledgeTierPerk from PledgeTierPerk pledgeTierPerk left join fetch pledgeTierPerk.tier where pledgeTierPerk.id =:id")
    Optional<PledgeTierPerk> findOneWithToOneRelationships(@Param("id") Long id);
}
