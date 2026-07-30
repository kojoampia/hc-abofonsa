package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WaitlistSignup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WaitlistSignupRepository extends JpaRepository<WaitlistSignup, Long>, JpaSpecificationExecutor<WaitlistSignup> {}
