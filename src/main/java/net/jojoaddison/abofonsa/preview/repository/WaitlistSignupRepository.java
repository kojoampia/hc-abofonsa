package net.jojoaddison.abofonsa.preview.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.WaitlistSignup;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WaitlistSignup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WaitlistSignupRepository extends JpaRepository<WaitlistSignup, Long>, JpaSpecificationExecutor<WaitlistSignup> {
    /**
     * Lookup for the duplicate check. Keyed on the normalised address, which is the column carrying
     * the unique constraint — querying {@code email} instead would miss "Ama@Clinic.org" when
     * "ama@clinic.org" is already on the list, which is the entire point of storing both.
     */
    Optional<WaitlistSignup> findByEmailNormalized(String emailNormalized);

    Optional<WaitlistSignup> findByConfirmationToken(String confirmationToken);

    /**
     * Unsubscribing has its own credential. Sharing the confirmation token meant one leaked link
     * granted both, and that a link with no expiry could still remove somebody years later.
     */
    Optional<WaitlistSignup> findByUnsubscribeToken(String unsubscribeToken);

    /**
     * How many signups this client has produced since a cutoff. Counted against the salted IP hash
     * rather than the address, because an abuser varies the address and not the socket.
     */
    long countByIpHashAndCapturedAtAfter(String ipHash, Instant cutoff);

    long countByStatus(net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus status);

    /**
     * Rows for a CSV export, oldest first.
     *
     * <p>Every filter is optional and null means "no restriction", so one query serves the whole
     * drill-down rather than needing a method per combination.
     */
    @Query(
        """
        select s from WaitlistSignup s
        where (:status is null or s.status = :status)
          and (cast(:from as instant) is null or s.capturedAt >= :from)
          and (cast(:to as instant) is null or s.capturedAt < :to)
        order by s.capturedAt
        """
    )
    List<WaitlistSignup> findForExport(
        @Param("status") net.jojoaddison.abofonsa.preview.domain.enumeration.SignupStatus status,
        @Param("from") Instant from,
        @Param("to") Instant to
    );

    /**
     * Unsubscribes per time bucket, for the WAITLIST_UNSUBSCRIBED series.
     *
     * <p>Read from this table rather than from {@code capture_event} because unsubscribing is a
     * state change on a signup, not a page interaction — the link is usually opened from an email
     * client, and there is no visit to attribute it to.
     */
    @Query(
        nativeQuery = true,
        value = """
        select extract(epoch from date_trunc(cast(:unit as text), unsubscribed_at)) as bucket_epoch, count(*) as total
        from waitlist_signup
        where unsubscribed_at is not null
          and unsubscribed_at >= :from
          and unsubscribed_at < :to
        group by 1
        order by 1
        """
    )
    List<Object[]> countUnsubscribesByBucket(@Param("unit") String unit, @Param("from") Instant from, @Param("to") Instant to);
}
